package name.jgn196.passwords.manager;

import java.io.IOException;
import java.nio.file.Files;

import static name.jgn196.passwords.manager.Manager.STORE_FILE;

class Preconditions {

    private Preconditions() { }

    static void givenNoDataFile() throws IOException {

        if (STORE_FILE.exists())
            Files.delete(STORE_FILE.toPath());
    }

    static StoreBuilder givenStoreWithPassword(final String password) throws IOException {

        givenNoDataFile();
        return new StoreBuilder(password);
    }
}
