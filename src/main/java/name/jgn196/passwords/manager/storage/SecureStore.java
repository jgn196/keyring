package name.jgn196.passwords.manager.storage;

import java.util.stream.Stream;

public abstract class SecureStore {

    public abstract void store(final StoreEntry entry) throws EntryNotStored;

    public abstract Stream<StoreEntry> stream();
}