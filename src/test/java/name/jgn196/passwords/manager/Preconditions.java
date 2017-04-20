package name.jgn196.passwords.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class Preconditions {

    private Preconditions() { }

    static void givenNoDataFile() throws IOException {

        if (Files.exists(Paths.get(STORE_FILE_NAME)))
            Files.delete(Paths.get(STORE_FILE_NAME));
    }

    static StoreBuilder givenStoreWithPassword(final String password) throws IOException {

        givenNoDataFile();
        return new StoreBuilder(password);
    }
}
