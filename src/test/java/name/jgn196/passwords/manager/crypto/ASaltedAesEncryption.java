package name.jgn196.passwords.manager.crypto;

import name.jgn196.passwords.manager.core.Password;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;

public class ASaltedAesEncryption {

    private static final Salt SALT = new SaltGenerator().get();

    @Test
    public void encryptsDataFromPassword() {

        final byte[] plainText = "plain text".getBytes();
        final byte[] result = new SaltedAesEncryption(Password.from("password")).encryptWithSalt(plainText, SALT);

        assertThat(result.length, greaterThanOrEqualTo(plainText.length));
        assertFalse("Result contains plain text.", contains(result, plainText));
    }

    @Test
    public void decryptsUsingPassword() {

        final StoreEncryption encryption = new SaltedAesEncryption(Password.from("password"));
        final byte[] plainText = "plain text".getBytes();
        final byte[] cipherText = encryption.encryptWithSalt(plainText, SALT);

        assertArrayEquals(plainText, encryption.decryptWithSalt(cipherText, SALT));
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsUsingWrongPassword() {

        final byte[] plainText = "plain text".getBytes();
        final byte[] cipherText = new SaltedAesEncryption(Password.from("password")).encryptWithSalt(plainText, SALT);

        new SaltedAesEncryption(Password.from("p@ssword")).decryptWithSalt(cipherText, SALT);
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsCorruptSalt() {

        final byte[] plainText = "plain text".getBytes();
        final StoreEncryption encryption = new SaltedAesEncryption(Password.from("password"));
        final byte[] cipherText = encryption.encryptWithSalt(plainText, SALT);
        final Salt corruptSalt = corrupted();

        encryption.decryptWithSalt(cipherText, corruptSalt);
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsCorruptCipherText() {

        final byte[] plainText = "plain text".getBytes();
        final StoreEncryption encryption = new SaltedAesEncryption(Password.from("password"));
        final byte[] cipherText = encryption.encryptWithSalt(plainText, SALT);
        cipherText[8] ^= 0xFF;

        encryption.decryptWithSalt(cipherText, SALT);
    }

    @Test
    public void closesItsPassword() {

        final Password password = Password.from("password");

        new SaltedAesEncryption(password).close();

        assertTrue(password.isClosed());
    }

    private boolean contains(final byte[] searchIn, final byte[] searchTerm) {

        return rangeClosed(0, searchIn.length - searchTerm.length)
                .mapToObj(i -> copyOfRange(searchIn, i, i + searchTerm.length))
                .anyMatch(subset -> Arrays.equals(subset, searchTerm));
    }

    private Salt corrupted() {

        final byte[] buffer = copyOf(SALT.toBytes(), Salt.SALT_SIZE);
        buffer[0] ^= 0xFF;

        return new Salt(buffer);
    }
}
