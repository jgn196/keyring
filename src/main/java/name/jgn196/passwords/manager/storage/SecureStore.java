package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Password;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

public interface SecureStore {

    Stream<StoreEntry> readEntriesUsing(Password password) throws IOException;
    void writeEntries(Collection<StoreEntry> entries, Password password) throws IOException;
}