package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;

class StoreFile {

    static final String STORE_FILE_NAME = "passwords.dat";

    static boolean exists() {

        return Paths.get(STORE_FILE_NAME).toFile().exists();
    }

    static FileStore openWithPassword(final Password password) {

        return new FileStore(Paths.get(STORE_FILE_NAME).toFile(), password);
    }
}
