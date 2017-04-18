package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import java.io.*;
import java.security.SecureRandom;
import java.util.zip.CRC32;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

class StoreEncryption implements AutoCloseable {

    private static final int ITERATIONS = 20;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int SALT_SIZE = 8;
    private static final CRC32 CRC_32 = new CRC32();
    private static final int CHECKSUM_SIZE = 4;
    private static final boolean ENCRYPTING = true;
    private static final boolean DECRYPTING = false;
    private static final long WORD_32_BIT_MASK = 0xFFFFFFFFL;

    private final Password password;

    StoreEncryption(final Password password) {

        this.password = password;
    }

    byte[] encrypt(final byte[] plainText) {

        try (final ByteArrayOutputStream result = new ByteArrayOutputStream();
             final DataOutputStream out = new DataOutputStream(result)) {

            final byte[] salt = generatedSalt();

            out.write(salt);
            out.writeInt((int) checksumOf(plainText));
            out.write(encryptWithSalt(plainText, salt));

            return result.toByteArray();

        } catch (IOException |
                InvalidCipherTextException e) {

            throw new RuntimeException(e);
        }
    }

    private byte[] generatedSalt() {

        final byte[] result = new byte[SALT_SIZE];
        SECURE_RANDOM.nextBytes(result);

        return result;
    }

    private long checksumOf(final byte[] plainText) {

        CRC_32.reset();
        CRC_32.update(plainText, 0, plainText.length);
        return CRC_32.getValue();
    }

    private byte[] encryptWithSalt(final byte[] plainText, final byte[] salt) throws InvalidCipherTextException {

        final PKCS12ParametersGenerator parametersGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
        parametersGenerator.init(
                PBEParametersGenerator.PKCS12PasswordToBytes(password.characters()),
                salt,
                ITERATIONS);

        final CBCBlockCipher blockCipher = new CBCBlockCipher(new AESEngine());

        final PaddedBufferedBlockCipher paddedCipher = new PaddedBufferedBlockCipher(blockCipher, new PKCS7Padding());
        paddedCipher.init(ENCRYPTING, parametersGenerator.generateDerivedParameters(256, 128));

        final byte[] result = new byte[paddedCipher.getOutputSize(plainText.length)];

        int bytesCopied = paddedCipher.processBytes(plainText, 0, plainText.length, result, 0);
        paddedCipher.doFinal(result, bytesCopied);

        return result;
    }

    byte[] decrypt(final byte[] encryptedData) {
        try (final DataInputStream in = new DataInputStream(new ByteArrayInputStream(encryptedData))) {

            final byte[] salt = readSaltFrom(in);
            final long checksum = readChecksumFrom(in);
            final byte[] plainText = decryptWithSalt(salt, readCipherTextFrom(encryptedData));

            if (checksum != checksumOf(plainText))
                throw new DecryptionFailed("Decrypted data failed hash test.");

            return plainText;

        } catch (IOException e) {

            throw new RuntimeException(e);
        } catch (InvalidCipherTextException e) {

            throw new DecryptionFailed(e);
        }
    }

    private byte[] readSaltFrom(final DataInputStream in) throws IOException {

        final byte[] salt = new byte[SALT_SIZE];
        if (in.read(salt) != SALT_SIZE)
            throw new IOException("Failed to read SALT from in memory stream.");

        return salt;
    }

    private long readChecksumFrom(final DataInputStream in) throws IOException {

        return in.readInt() & WORD_32_BIT_MASK;
    }

    private byte[] readCipherTextFrom(final byte[] encryptedData) throws IOException {

        return copyOfRange(encryptedData, SALT_SIZE + CHECKSUM_SIZE, encryptedData.length);
    }

    private byte[] decryptWithSalt(final byte[] salt, final byte[] cipherText) throws InvalidCipherTextException {

        final PKCS12ParametersGenerator parametersGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
        parametersGenerator.init(
                PBEParametersGenerator.PKCS12PasswordToBytes(password.characters()),
                salt,
                ITERATIONS);

        final CBCBlockCipher blockCipher = new CBCBlockCipher(new AESEngine());

        final PaddedBufferedBlockCipher paddedCipher = new PaddedBufferedBlockCipher(blockCipher, new PKCS7Padding());
        paddedCipher.init(DECRYPTING, parametersGenerator.generateDerivedParameters(256, 128));

        final byte[] buffer = new byte[paddedCipher.getOutputSize(cipherText.length)];

        int bytesCopied = paddedCipher.processBytes(cipherText, 0, cipherText.length, buffer, 0);
        int plainTextLength = bytesCopied + paddedCipher.doFinal(buffer, bytesCopied);

        return copyOf(buffer, plainTextLength);
    }

    @Override
    public void close() {

        password.close();
    }
}