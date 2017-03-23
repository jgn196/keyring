package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import org.junit.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class AStoreFormat {

    @Test
    public void serialisesEntries() throws IOException {

        final StoreFormat format = new StoreFormat();
        final StoreEntry[] entries = new StoreEntry[]{
                new StoreEntry(new Login("system 1", "user 1"), Password.from("password 1")),
                new StoreEntry(new Login("system 2", "user 2"), Password.from("password 2")),
                new StoreEntry(new Login("system 3", "user 3"), Password.from("password 3"))
        };

        final byte[] formatted = format.serialise(asList(entries));

        assertThat(format.deserialiseEntries(formatted), hasItems(entries));
    }
}
