package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.crypto.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class FileStore implements SecureStore {

    private final File file;
    private final StoreFormat format = new StoreFormat();
    private final StoreEncryption encryption;

    private FileIO io = new FileIO.Implementation();

    public FileStore(final File file, final Password filePassword) {

        this.file = file;
        encryption = new SaltedAesEncryption(filePassword);
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

        final StoreFormat.DeserialiseResult result = format.deserialiseOuterLayer(io.readAllFrom(file));
        final byte[] pl = encryption.decryptWithSalt(result.ct(), result.salt());
        if (!result.crc().equals(Crc32.of(pl)))
            throw new DecryptionFailed("Decrypted data failed hash test.");

        return new HashSet<>(format.deserialiseEntries(pl));
    }

    private void save(final Set<StoreEntry> entries) throws IOException {

        final byte[] pl = format.serialise(entries);
        final Crc32 crc = Crc32.of(pl);
        final Salt salt = new SaltGenerator().get();
        final byte[] ct = encryption.encryptWithSalt(pl, salt);
        io.writeAllTo(file, format.serialise(salt, crc, ct));
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

        try {

            final Set<StoreEntry> entries = storeContents();
            entries.remove(entry);
            save(entries);
        } catch (IOException e) {

            throw new EntryNotRemoved(e);
        }
    }

    @Override
    public void changePasswordTo(final Password password) { }

    @Override
    public void close() {

        try {
            encryption.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

