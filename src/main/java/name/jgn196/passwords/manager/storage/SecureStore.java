package name.jgn196.passwords.manager.storage;

import java.util.stream.Stream;

public interface SecureStore extends AutoCloseable {

    void store(final StoreEntry entry) throws EntryNotStored;

    Stream<StoreEntry> stream();

    void remove(StoreEntry entry);

    void copyTo(SecureStore destination);

    void replaceWith(SecureStore store);
}