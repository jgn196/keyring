package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.storage.FileStore;

import java.io.File;
import java.nio.file.Path;

class StoreFile {

    private final File file;

    StoreFile(final String fileName) {

        this.file = new File(fileName);
    }

    boolean exists() {

        return file.exists();
    }

    Path toPath() {

        return file.toPath();
    }

    FileStore openWithPassword(final Password password) {

        return new FileStore(file, password);
    }
}
