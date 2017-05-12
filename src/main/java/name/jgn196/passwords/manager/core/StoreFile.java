package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.crypto.SaltedAesEncryption;
import name.jgn196.passwords.manager.storage.FileStore;
import name.jgn196.passwords.manager.storage.FileStoreBuilder;
import name.jgn196.passwords.manager.storage.StoreFormat;

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

    FileStore openWithPassword(final Password password) {

        return new FileStoreBuilder()
                .with(file)
                .with(new StoreFormat())
                .with(new SaltedAesEncryption())
                .with(password)
                .build();
    }
}
