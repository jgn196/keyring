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
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

class StoreEncryption {

    private static final int ITERATIONS = 20;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int SALT_SIZE = 8;

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
            cipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, ITERATIONS));

            out.write(salt);
            out.writeInt(Arrays.hashCode(plainText));
            out.write(cipher.doFinal(plainText));

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

    byte[] decrypt(final byte[] cipherText) {
        try (final DataInputStream in = new DataInputStream(new ByteArrayInputStream(cipherText))) {

            final byte[] salt = new byte[SALT_SIZE];
            in.read(salt);

            final int plainTextHash = in.readInt();

            final int cipherTextSize = cipherText.length - SALT_SIZE - 4;
            final byte[] ct = new byte[cipherTextSize];
            in.read(ct);
            cipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, ITERATIONS));

            final byte[] plainText = cipher.doFinal(ct);
            if (plainTextHash != Arrays.hashCode(plainText))
                throw new DecryptionFailed("Decrypted data failed hash test.");

            return plainText;

        } catch (InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException | IOException e) {

            throw new RuntimeException(e);
        } catch (BadPaddingException e) {

            throw new DecryptionFailed(e);
        }
    }
}