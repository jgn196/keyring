package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.storage.FileStore;
import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;

import java.util.Optional;
import java.util.stream.Stream;

public class Safe implements AutoCloseable {

    private SecureStore store;

    public Safe(final StoreFile file, final Password filePassword) {

        this(file.openWithPassword(filePassword));
    }

    Safe(final SecureStore store) {

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

    public boolean remove(final Login login) {

        final Optional<StoreEntry> entry = store.stream()
                .filter(e -> e.isFor(login))
                .findFirst();

        entry.ifPresent(store::remove);

        return entry.isPresent();
    }

    public void changePasswordTo(final Password newPassword) throws Exception {

        store.changePasswordTo(newPassword);
    }

    @Override
    public void close() throws Exception {

        store.close();
    }
}
