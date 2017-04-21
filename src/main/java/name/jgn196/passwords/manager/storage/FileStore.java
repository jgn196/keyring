package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class FileStore extends SecureStore implements AutoCloseable {

    private final File file;
    private final StoreFormat format = new StoreFormat();
    private final StoreEncryption encryption;

    private FileIO io = new FileIO.Implementation();

    public FileStore(final File file, final Password filePassword) {

        this.file = file;
        encryption = new StoreEncryption(filePassword);
    }

    void inject(final FileIO io) {

        this.io = io;
    }

    @Override
    public void store(final StoreEntry entry) throws EntryNotStored {
        try {

            final Set<StoreEntry> entries = storeContents();

            entries.stream()
                    .filter(e -> e.isFor(entry.login()))
                    .findFirst()
                    .ifPresent(entries::remove);

            entries.add(entry);
            save(entries);

        } catch (IOException e) {

            throw new EntryNotStored(e);
        }
    }

    private Set<StoreEntry> storeContents() throws IOException {

        if (!io.fileExists(file)) return new HashSet<>();

        return new HashSet<>(format.deserialise(encryption.decrypt(io.readAllFrom(file))));
    }

    private void save(final Set<StoreEntry> entries) throws IOException {

        io.writeAllTo(file, encryption.encrypt(format.serialise(entries)));
    }

    @Override
    public Stream<StoreEntry> stream() throws StoreReadFailed {
        try {

            return storeContents().stream();
        } catch (IOException e) {

            throw new StoreReadFailed(e);
        }
    }

    @Override
    public void remove(final StoreEntry entry) {

    }

    @Override
    public void close() {

        encryption.close();
    }
}

