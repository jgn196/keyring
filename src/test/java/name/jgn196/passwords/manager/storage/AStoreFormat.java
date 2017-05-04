package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.crypto.Crc32;
import name.jgn196.passwords.manager.crypto.Salt;
import name.jgn196.passwords.manager.crypto.SaltGenerator;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;
import static name.jgn196.passwords.manager.crypto.Salt.SALT_SIZE;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

public class AStoreFormat {

    private static final StoreFormat STORE_FORMAT = new StoreFormat();
    private static final StoreEntry[] STORE_ENTRIES = {
            new StoreEntry(new Login("system 1", "user 1"), Password.from("password 1")),
            new StoreEntry(new Login("system 2", "user 2"), Password.from("password 2")),
            new StoreEntry(new Login("system 3", "user 3"), Password.from("password 3"))
    };
    private static final Salt SALT = new SaltGenerator().get();
    private static final Crc32 CRC = Crc32.of(new byte[10]);
    private static final byte[] ENCRYPTED_ENTRIES = new byte[10];

    @Test
    public void serialisesEntries() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(asList(STORE_ENTRIES));

        assertThat(STORE_FORMAT.deserialiseEntries(formatted), hasItems(STORE_ENTRIES));
    }

    @Test
    public void writesFormatVersionToStartOfData() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(SALT, CRC, ENCRYPTED_ENTRIES);

        try (final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(formatted))) {

            assertEquals(StoreFormat.VERSION, inputStream.readInt());
        }
    }

    @Test
    public void writesSaltAfterVersion() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(SALT, CRC, ENCRYPTED_ENTRIES);

        try (final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(formatted))) {

            final long skipped = inputStream.skip(4);
            final Salt salt = Salt.readSaltFrom(inputStream);

            assertEquals(4, skipped);
            assertTrue(salt.equals(SALT));
        }
    }

    @Test
    public void writesCrcAfterSalt() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(SALT, CRC, ENCRYPTED_ENTRIES);

        try (final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(formatted))) {

            final long skipped = inputStream.skip(4 + SALT_SIZE);
            final Crc32 crc = Crc32.readFrom(inputStream);

            assertEquals(4 + SALT_SIZE, skipped);
            assertTrue(crc.equals(CRC));
        }
    }

    @Test
    public void writesEncryptedEntriesAfterCrc() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(SALT, CRC, ENCRYPTED_ENTRIES);

        try (final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(formatted))) {

            final long skipped = inputStream.skip(4 + SALT_SIZE + 4);
            final int ctSize = inputStream.readInt();
            final byte[] ct = new byte[ctSize];
            final int read = inputStream.read(ct);

            assertEquals(4 + SALT_SIZE + 4, skipped);
            assertEquals(ENCRYPTED_ENTRIES.length, ctSize);
            assertEquals(ctSize, read);
            assertArrayEquals(ENCRYPTED_ENTRIES, ct);
        }
    }

    @Test(expected = UnsupportedStoreFormat.class)
    public void willNotReadStoreWithIncorrectVersion() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(SALT, CRC, ENCRYPTED_ENTRIES);
        changeVersionField(formatted);

        STORE_FORMAT.deserialiseOuterLayer(formatted);
    }

    @Test(expected = IOException.class)
    public void throwsIfTruncatedData() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(SALT, CRC, ENCRYPTED_ENTRIES);
        final byte[] truncated = copyOfRange(formatted, 0, formatted.length - 1);

        STORE_FORMAT.deserialiseOuterLayer(truncated);
    }

    @Test
    public void deserialisesOuterLayer() throws IOException {

        final byte[] formatted = STORE_FORMAT.serialise(SALT, CRC, ENCRYPTED_ENTRIES);

        final StoreFormat.DeserialiseResult result = STORE_FORMAT.deserialiseOuterLayer(formatted);

        assertEquals(SALT, result.salt());
        assertEquals(CRC, result.crc());
        assertArrayEquals(ENCRYPTED_ENTRIES, result.ct());
    }

    private void changeVersionField(final byte[] formatted) {

        ByteBuffer.wrap(formatted).putInt(StoreFormat.VERSION + 1);
    }
}
