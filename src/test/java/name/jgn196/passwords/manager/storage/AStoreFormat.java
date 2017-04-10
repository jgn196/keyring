package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AStoreFormat {

    private static final StoreEntry[] STORE_ENTRIES = {
            new StoreEntry(new Login("system 1", "user 1"), Password.from("password 1")),
            new StoreEntry(new Login("system 2", "user 2"), Password.from("password 2")),
            new StoreEntry(new Login("system 3", "user 3"), Password.from("password 3"))
    };

    @Test
    public void serialisesEntries() throws IOException {

        final StoreFormat format = new StoreFormat();

        final byte[] formatted = format.serialise(asList(STORE_ENTRIES));

        assertThat(format.deserialise(formatted), hasItems(STORE_ENTRIES));
    }

    @Test
    public void writeFormatVersionToStartOfData() throws IOException {

        final byte[] formatted = new StoreFormat().serialise(asList(STORE_ENTRIES));

        try (final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(formatted))) {

            assertEquals(StoreFormat.VERSION, inputStream.readInt());
        }
    }

    @Test(expected = UnsupportedStoreFormat.class)
    public void willNotReadStoreWithIncorrectVersion() throws IOException {

        final byte[] formatted = new StoreFormat().serialise(asList(STORE_ENTRIES));
        formatted[0] ^= 0xff; // Change the leading version field

        new StoreFormat().deserialise(formatted);
    }
}
