package name.jgn196.passwords.manager.storage;

import java.util.stream.Stream;

public abstract class SecureStore implements AutoCloseable{

    public abstract void store(final StoreEntry entry);

    public abstract Stream<StoreEntry> stream();
}