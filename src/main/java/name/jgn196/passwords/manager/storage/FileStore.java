package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;
import name.jgn196.passwords.manager.crypto.Salt;
import name.jgn196.passwords.manager.crypto.SaltGenerator;
import name.jgn196.passwords.manager.crypto.StoreEncryption;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

public class FileStore implements SecureStore {

    private final File file;
    private final StoreFormat format;
    private final StoreEncryption encryption;
    private final FileIO io;

    public interface Importer {

        File file();
        StoreFormat storeFormat();
        StoreEncryption encryption();
        FileIO fileIO();
    }

    FileStore(final Importer importer) {

        file = importer.file();
        format = importer.storeFormat();
        encryption = importer.encryption();
        io = importer.fileIO();
    }

    @Override
    public Stream<StoreEntry> readEntriesUsing(final Password password) throws IOException {

        if (!io.fileExists(file)) return Stream.empty();

        final StoreFormat.DeserialiseResult result = format.deserialiseOuterLayer(io.readAllFrom(file));
        final byte[] pl = encryption.decryptWithSalt(result.ct(), result.salt(), password);
        if (!result.crc().equals(Crc32.of(pl)))
            throw new DecryptionFailed("Decrypted data failed hash test.");

        return format.deserialiseEntries(pl).stream();
    }

    @Override
    public void writeEntries(final Collection<StoreEntry> entries, final Password password) throws IOException {

        final byte[] pl = format.serialise(entries);
        final Crc32 crc = Crc32.of(pl);
        final Salt salt = new SaltGenerator().get();
        final byte[] ct = encryption.encryptWithSalt(pl, salt, password);
        final File tempFile = io.createTempFile();
        io.writeAllTo(tempFile, format.serialise(salt, crc, ct));
        io.move(tempFile, file);
    }
}
