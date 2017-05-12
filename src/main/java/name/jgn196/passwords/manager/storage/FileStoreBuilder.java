package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.crypto.StoreEncryption;

import java.io.File;

public class FileStoreBuilder {

    private File file;
    private StoreFormat format;
    private StoreEncryption encryption;
    private FileIO io = new FileIO.Implementation();
    private Password password;

    public FileStoreBuilder with(final File file) {

        this.file = file;
        return this;
    }

    public FileStoreBuilder with(final StoreFormat format) {

        this.format = format;
        return this;
    }

    public FileStoreBuilder with(final StoreEncryption encryption) {

        this.encryption = encryption;
        return this;
    }

    FileStoreBuilder with(final FileIO io) {

        this.io = io;
        return this;
    }

    public FileStoreBuilder with(final Password password) {

        this.password = password;
        return this;
    }

    public FileStore build() {

        return new FileStore(new FileStore.Importer() {
            @Override
            public File file() { return file; }

            @Override
            public StoreFormat storeFormat() { return format; }

            @Override
            public StoreEncryption encryption() { return encryption; }

            @Override
            public FileIO fileIO() { return io; }

            @Override
            public Password password() { return password; }
        });
    }
}
