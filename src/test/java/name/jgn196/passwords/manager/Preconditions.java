package name.jgn196.passwords.manager;

import java.io.IOException;
import java.nio.file.Files;

class Preconditions {

    private Preconditions() { }

    static void givenNoDataFile() throws IOException {

        if (StoreFile.exists())
            Files.delete(StoreFile.toPath());
    }

    static StoreBuilder givenStoreWithPassword(final String password) throws IOException {

        givenNoDataFile();
        return new StoreBuilder(password);
    }
}
