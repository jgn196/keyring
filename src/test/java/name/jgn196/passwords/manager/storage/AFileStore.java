package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AFileStore {

    private File testFile;

    @Before
    public void pickTestFileName() throws IOException {

        testFile = File.createTempFile("pass_test", ".dat");
        testFile.delete();
    }

    @Test
    public void storesEntries() throws Exception {

        try (final FileStore store = new FileStore(testFile, Password.from("secret"))) {

            store.store(new StoreEntry(new Login("www.site.com", "Bill"), Password.from("another secret")));

            assertTrue("Store file hasn't been created.", testFile.exists());
        }

        try (final FileStore store = new FileStore(testFile, Password.from("secret"))) {

            assertThat(
                    store.stream().collect(toList()),
                    hasItem(new StoreEntry(new Login("www.site.com", "Bill"), Password.from("another secret"))));
        }
    }

    @After
    public void deleteTestFile() {

        testFile.delete();
    }
}
