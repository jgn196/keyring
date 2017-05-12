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
    private static final byte[] PLAIN_TEXT = "plain text".getBytes();

    @Test
    public void encryptsDataFromPassword() {

        final byte[] result = new SaltedAesEncryption().encryptWithSalt(PLAIN_TEXT, SALT, Password.from("password"));

        assertThat(result.length, greaterThanOrEqualTo(PLAIN_TEXT.length));
        assertFalse("Result contains plain text.", contains(result, PLAIN_TEXT));
    }

    @Test
    public void decryptsUsingPassword() {

        final StoreEncryption encryption = new SaltedAesEncryption();
        final byte[] cipherText = encryption.encryptWithSalt(PLAIN_TEXT, SALT, Password.from("password"));

        assertArrayEquals(PLAIN_TEXT, encryption.decryptWithSalt(cipherText, SALT, Password.from("password")));
    }

    @Test(expected = DecryptionFailed.class)
    public void decryptsUsingWrongPassword() {

        final byte[] cipherText = new SaltedAesEncryption().encryptWithSalt(PLAIN_TEXT, SALT, Password.from("password"));

        new SaltedAesEncryption().decryptWithSalt(cipherText, SALT, Password.from("p@ssword"));
    }

    @Test(expected = DecryptionFailed.class)
    public void failsToDecryptCorruptSalt() {

        final StoreEncryption encryption = new SaltedAesEncryption();
        final byte[] cipherText = encryption.encryptWithSalt(PLAIN_TEXT, SALT, Password.from("password"));
        final Salt corruptSalt = corrupted();

        encryption.decryptWithSalt(cipherText, corruptSalt, Password.from("password"));
    }

    @Test(expected = DecryptionFailed.class)
    public void failsToDecryptCorruptCipherText() {

        final StoreEncryption encryption = new SaltedAesEncryption();
        final byte[] cipherText = encryption.encryptWithSalt(PLAIN_TEXT, SALT, Password.from("password"));
        cipherText[8] ^= 0xFF;

        encryption.decryptWithSalt(cipherText, SALT, Password.from("password"));
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
