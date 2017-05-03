package name.jgn196.passwords.manager.crypto;

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

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static name.jgn196.passwords.manager.crypto.Salt.SALT_SIZE;
import static name.jgn196.passwords.manager.crypto.Salt.readSaltFrom;

public class SaltedAesEncryption implements StoreEncryption {

    private static final int ITERATIONS = 20;
    private static final int CHECKSUM_SIZE = 4;
    private static final boolean ENCRYPTING = true;
    private static final boolean DECRYPTING = false;

    private final Password password;

    public SaltedAesEncryption(final Password password) {

        this.password = password;
    }

    @Override
    public byte[] encrypt(final byte[] plainText) {

        try (final ByteArrayOutputStream result = new ByteArrayOutputStream();
             final DataOutputStream out = new DataOutputStream(result)) {

            final Salt salt = new Salt();
            final Crc32 crc = Crc32.of(plainText);

            salt.writeTo(out);
            crc.writeTo(out);
            out.write(encryptWithSalt(plainText, salt));

            return result.toByteArray();

        } catch (IOException |
                InvalidCipherTextException e) {

            throw new RuntimeException(e);
        }
    }

    private byte[] encryptWithSalt(final byte[] plainText, final Salt salt) throws InvalidCipherTextException {

        final PKCS12ParametersGenerator parametersGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
        parametersGenerator.init(
                PBEParametersGenerator.PKCS12PasswordToBytes(password.characters()),
                salt.toBytes(),
                ITERATIONS);

        final CBCBlockCipher blockCipher = new CBCBlockCipher(new AESEngine());

        final PaddedBufferedBlockCipher paddedCipher = new PaddedBufferedBlockCipher(blockCipher, new PKCS7Padding());
        paddedCipher.init(ENCRYPTING, parametersGenerator.generateDerivedParameters(256, 128));

        final byte[] result = new byte[paddedCipher.getOutputSize(plainText.length)];

        int bytesCopied = paddedCipher.processBytes(plainText, 0, plainText.length, result, 0);
        paddedCipher.doFinal(result, bytesCopied);

        return result;
    }

    @Override
    public byte[] decrypt(final byte[] encryptedData) {
        try (final DataInputStream in = new DataInputStream(new ByteArrayInputStream(encryptedData))) {

            final Salt salt = readSaltFrom(in);
            final Crc32 crc = Crc32.readFrom(in);
            final byte[] plainText = decryptWithSalt(salt, readCipherTextFrom(encryptedData));

            if (!crc.equals(Crc32.of(plainText)))
                throw new DecryptionFailed("Decrypted data failed hash test.");

            return plainText;

        } catch (IOException e) {

            throw new RuntimeException(e);
        } catch (InvalidCipherTextException e) {

            throw new DecryptionFailed(e);
        }
    }

    private byte[] readCipherTextFrom(final byte[] encryptedData) throws IOException {

        return copyOfRange(encryptedData, SALT_SIZE + CHECKSUM_SIZE, encryptedData.length);
    }

    private byte[] decryptWithSalt(final Salt salt, final byte[] cipherText) throws InvalidCipherTextException {

        final PKCS12ParametersGenerator parametersGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
        parametersGenerator.init(
                PBEParametersGenerator.PKCS12PasswordToBytes(password.characters()),
                salt.toBytes(),
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