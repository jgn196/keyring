package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.storage.FileStore;

import java.io.File;
import java.nio.file.Path;

public class StoreFile {

    private final File file;

    public StoreFile(final String fileName) {

        this.file = new File(fileName);
    }

    public boolean exists() {

        return file.exists();
    }

    public Path toPath() {

        return file.toPath();
    }

    public FileStore openWithPassword(final Password password) {

        return new FileStore(file, password);
    }
}
