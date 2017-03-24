package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.copyOfRange;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;

public class AStoreEncryption {

    @Test
    public void encryptsDataFromPassword() {

        final byte[] plainText = "plain text".getBytes();
        final byte[] result = new StoreEncryption(Password.from("password")).encrypt(plainText);

        assertThat(result.length, greaterThanOrEqualTo(plainText.length));
        assertFalse("Result contains plain text.", contains(result, plainText));
    }

    @Test
    public void decryptsUsingPassword() {

        final StoreEncryption encryption = new StoreEncryption(Password.from("password"));
        final byte[] plainText = "plain text".getBytes();
        final byte[] cipherText = encryption.encrypt(plainText);

        assertArrayEquals(plainText, encryption.decrypt(cipherText));
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsUsingWrongPassword() {

        final byte[] plainText = "plain text".getBytes();
        final byte[] cipherText = new StoreEncryption(Password.from("password")).encrypt(plainText);

        new StoreEncryption(Password.from("p@ssword")).decrypt(cipherText);
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsCorruptSalt() {

        final byte[] plainText = "plain text".getBytes();
        final StoreEncryption encryption = new StoreEncryption(Password.from("password"));
        final byte[] cipherText = encryption.encrypt(plainText);
        cipherText[0] ^= 0xFF;

        encryption.decrypt(cipherText);
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsCorruptCipherText() {

        final byte[] plainText = "plain text".getBytes();
        final StoreEncryption encryption = new StoreEncryption(Password.from("password"));
        final byte[] cipherText = encryption.encrypt(plainText);
        cipherText[8] ^= 0xFF;

        encryption.decrypt(cipherText);
    }

    private boolean contains(final byte[] searchIn, final byte[] searchTerm) {

        return rangeClosed(0, searchIn.length - searchTerm.length)
                .mapToObj(i -> copyOfRange(searchIn, i, i + searchTerm.length))
                .anyMatch(subset -> Arrays.equals(subset, searchTerm));
    }
}
