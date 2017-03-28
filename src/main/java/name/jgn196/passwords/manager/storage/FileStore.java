package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class FileStore extends SecureStore {

    private final File file;
    private final StoreFormat format = new StoreFormat();
    private final Password filePassword; // TODO - Implement encryption

    public FileStore(final File file, final Password filePassword) {

        this.file = file;
        this.filePassword = filePassword;
    }

    @Override
    public void store(final StoreEntry entry) throws EntryNotStored {
        try {

            final Set<StoreEntry> entries = storeContents();
            entries.add(entry);
            save(entries);

        } catch (IOException e) {

            throw new EntryNotStored(e);
        }
    }

    private Set<StoreEntry> storeContents() throws IOException {

        if (!file.exists()) return new HashSet<>();

        return new HashSet<>(format.deserialiseEntries(Files.readAllBytes(file.toPath())));
    }

    private void save(final Set<StoreEntry> entries) throws IOException {

        Files.write(file.toPath(), format.serialise(entries));
    }

    @Override
    public Stream<StoreEntry> stream() {
        try {

            return storeContents().stream();
        } catch (IOException e) {

            e.printStackTrace();
            return Stream.empty();
        }
    }

    @Override
    public void close() throws Exception {

        // TODO - Wipe password
    }
}