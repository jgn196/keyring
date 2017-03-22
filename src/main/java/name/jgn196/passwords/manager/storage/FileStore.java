package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

class FileStore extends SecureStore {

    private final File file;
    private final Password filePassword; // TODO - Implement encryption

    FileStore(final File file, final Password filePassword) {

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

        try (final DataInputStream in = new DataInputStream(new FileInputStream(file))) {

            final EntryDeserialiser deserialiser = new EntryDeserialiser(in);
            final int entryCount = in.readInt();
            final HashSet<StoreEntry> results = new HashSet<>();
            for (int i = 0; i < entryCount; i++) {

                results.add(deserialiser.deserialise());
            }

            return results;
        }
    }

    private void save(final Set<StoreEntry> entries) throws IOException {

        try (final DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {

            final EntrySerialiser serialiser = new EntrySerialiser(out);

            out.writeInt(entries.size());
            for (final StoreEntry entry : entries) {

                serialiser.serialise(entry);
            }
        }
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