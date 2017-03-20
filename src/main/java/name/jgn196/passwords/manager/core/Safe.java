package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;

public class Safe implements AutoCloseable {

    private final SecureStore store;

    public Safe(final SecureStore store) {

        this.store = store;
    }

    public void close() throws Exception {

    }

    public void store(final Login login, final Password password) {

        store.store(new StoreEntry(login, password));
    }
}
