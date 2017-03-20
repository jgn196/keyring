package name.jgn196.passwords.manager.storage;

import java.util.stream.Stream;

public class SecureStore implements AutoCloseable{

    public void store(final StoreEntry entry) {

    }

    public Stream<StoreEntry> stream() {

        return null;
    }

    @Override
    public void close() {
    }
}
