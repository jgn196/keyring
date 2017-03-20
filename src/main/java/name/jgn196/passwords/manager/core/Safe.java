package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;

import java.util.Optional;
import java.util.stream.Stream;

public class Safe implements AutoCloseable {

    private final SecureStore store;

    public Safe(final SecureStore store) {

        this.store = store;
    }

    public void store(final Login login, final Password password) {

        store.store(new StoreEntry(login, password));
    }

    public Optional<Password> passwordFor(final Login login) {

        return store.stream()
                .filter(entry -> entry.isFor(login))
                .map(StoreEntry::password)
                .findFirst();
    }

    public Stream<Login> logins() {

        return store.stream().map(StoreEntry::login);
    }

    public void close() throws Exception {

        store.close();
    }
}
