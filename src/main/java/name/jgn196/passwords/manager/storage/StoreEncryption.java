package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static java.util.Arrays.copyOfRange;

class StoreEncryption {

    private static final int ITERATIONS = 20;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int SALT_SIZE = 8;
    private static final int CHECKSUM_SIZE = 8;
    static final CRC32 CRC_32 = new CRC32();

    private final Cipher cipher;
    private final SecretKey key;

    StoreEncryption(final Password password) {
        try {

            cipher = Cipher.getInstance("PBEWithMD5AndDES");
            key = SecretKeyFactory
                    .getInstance("PBEWithMD5AndDES")
                    .generateSecret(new PBEKeySpec(password.characters()));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] encrypt(final byte[] plainText) {

        try (final ByteArrayOutputStream result = new ByteArrayOutputStream();
             final DataOutputStream out = new DataOutputStream(result)) {

            final byte[] salt = generatedSalt();

            out.write(salt);
            out.writeLong(checksumOf(plainText));
            out.write(encryptWithSalt(plainText, salt));

            return result.toByteArray();

        } catch (IOException |
                BadPaddingException |
                IllegalBlockSizeException |
                InvalidKeyException |
                InvalidAlgorithmParameterException e) {

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

    private byte[] encryptWithSalt(final byte[] plainText, final byte[] salt) throws
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        cipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, ITERATIONS));
        return cipher.doFinal(plainText);
    }

    byte[] decrypt(final byte[] encryptedData) {
        try (final DataInputStream in = new DataInputStream(new ByteArrayInputStream(encryptedData))) {

            final byte[] salt = readSaltFrom(in);
            final long checksum = in.readLong();
            final byte[] plainText = decryptWithSalt(salt, readCipherTextFrom(encryptedData));

            if (checksum != checksumOf(plainText))
                throw new DecryptionFailed("Decrypted data failed hash test.");

            return plainText;

        } catch (InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException | IOException e) {

            throw new RuntimeException(e);
        } catch (BadPaddingException e) {

            throw new DecryptionFailed(e);
        }
    }

    private byte[] readSaltFrom(final DataInputStream in) throws IOException {

        final byte[] salt = new byte[SALT_SIZE];
        if (in.read(salt) != SALT_SIZE)
            throw new IOException("Failed to read SALT from in memory stream.");

        return salt;
    }

    private byte[] readCipherTextFrom(final byte[] encryptedData) throws IOException {

        return copyOfRange(encryptedData, SALT_SIZE + CHECKSUM_SIZE, encryptedData.length);
    }

    private byte[] decryptWithSalt(final byte[] salt, final byte[] cipherText) throws
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        cipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, ITERATIONS));
        return cipher.doFinal(cipherText);
    }
}