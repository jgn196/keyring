package name.jgn196.passwords.manager.crypto;

import name.jgn196.passwords.manager.core.Password;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;

public class ASaltedAesEncryption {

    private static final Salt SALT = new SaltGenerator().get();
    static final byte[] PLAIN_TEXT = "plain text".getBytes();

    @Test
    public void encryptsDataFromPassword() {

        final byte[] result = new SaltedAesEncryption(Password.from("password")).encryptWithSalt(PLAIN_TEXT, SALT);

        assertThat(result.length, greaterThanOrEqualTo(PLAIN_TEXT.length));
        assertFalse("Result contains plain text.", contains(result, PLAIN_TEXT));
    }

    @Test
    public void decryptsUsingPassword() {

        final StoreEncryption encryption = new SaltedAesEncryption(Password.from("password"));
        final byte[] cipherText = encryption.encryptWithSalt(PLAIN_TEXT, SALT);

        assertArrayEquals(PLAIN_TEXT, encryption.decryptWithSalt(cipherText, SALT));
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsUsingWrongPassword() {

        final byte[] cipherText = new SaltedAesEncryption(Password.from("password")).encryptWithSalt(PLAIN_TEXT, SALT);

        new SaltedAesEncryption(Password.from("p@ssword")).decryptWithSalt(cipherText, SALT);
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsCorruptSalt() {

        final StoreEncryption encryption = new SaltedAesEncryption(Password.from("password"));
        final byte[] cipherText = encryption.encryptWithSalt(PLAIN_TEXT, SALT);
        final Salt corruptSalt = corrupted();

        encryption.decryptWithSalt(cipherText, corruptSalt);
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsCorruptCipherText() {

        final StoreEncryption encryption = new SaltedAesEncryption(Password.from("password"));
        final byte[] cipherText = encryption.encryptWithSalt(PLAIN_TEXT, SALT);
        cipherText[8] ^= 0xFF;

        encryption.decryptWithSalt(cipherText, SALT);
    }

    @Test
    public void canChangeItsPassword() {

        final Password firstPassword = Password.from("password");
        final Password secondPassword = Password.from("password 2");
        final byte[] cipherText = new SaltedAesEncryption(secondPassword).encryptWithSalt(PLAIN_TEXT, SALT);

        final StoreEncryption encryption = new SaltedAesEncryption(firstPassword);

        try {
            encryption.decryptWithSalt(cipherText, SALT);
            fail("Decrypted with wrong password");
        } catch(DecryptionFailed e) {
            // Expected
        }

        encryption.changePassword(secondPassword);
        assertArrayEquals(PLAIN_TEXT, encryption.decryptWithSalt(cipherText, SALT));
        assertTrue(firstPassword.isClosed());
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
