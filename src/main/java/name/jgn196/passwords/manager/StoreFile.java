package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Path;
import java.nio.file.Paths;

class StoreFile {

    private static final String STORE_FILE_NAME = "passwords.dat";

    // This class is not for instantiation
    private StoreFile() { }

    static boolean exists() {

        return toPath().toFile().exists();
    }

    static Path toPath() {

        return Paths.get(STORE_FILE_NAME);
    }

    static FileStore openWithPassword(final Password password) {

        return new FileStore(toPath().toFile(), password);
    }
}
