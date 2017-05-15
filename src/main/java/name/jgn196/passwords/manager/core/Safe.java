package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.crypto.DecryptionFailed;
import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Safe implements AutoCloseable {

    private SecureStore store;
    private Password filePassword;

    public Safe(final StoreFile file, final Password filePassword) {

        this(file.open(), filePassword);
    }

    Safe(final SecureStore store, final Password filePassword) {

        this.store = store;
        this.filePassword = filePassword;
    }

    public void store(final Login login, final Password password) {
        try {

            final Map<Login, StoreEntry> entries = store
                    .readEntriesUsing(filePassword)
                    .collect(Collectors.toMap(StoreEntry::login, e -> e));
            final StoreEntry entry = new StoreEntry(login, password);

            entries.put(login, entry);

            store.writeEntries(entries.values(), filePassword);

        } catch (IOException e) {
            throw new EntryNotStored(e);
        }
    }

    public Optional<Password> passwordFor(final Login login) {
        try {

            return store.readEntriesUsing(filePassword)
                    .filter(entry -> entry.isFor(login))
                    .map(StoreEntry::password)
                    .findFirst();

        } catch (IOException e) {
            throw new EntriesNotRead(e);
        }
    }

    public Stream<Login> logins() {
        try {

            return store.readEntriesUsing(filePassword).map(StoreEntry::login);

        } catch (IOException e) {
            throw new EntriesNotRead(e);
        }
    }

    public boolean remove(final Login login) {
        try {

            final Collection<StoreEntry> entries = store.readEntriesUsing(filePassword).collect(toList());
            final boolean entriesRemoved = entries.removeIf(e -> e.isFor(login));

            store.writeEntries(entries, filePassword);
            return entriesRemoved;

        } catch (IOException e) {
            throw new EntryNotRemoved(e);
        }
    }

    public void changePasswordTo(final Password newPassword)  {
        try {

            if (newPassword.equals(filePassword)) return;

            store.writeEntries(store.readEntriesUsing(filePassword).collect(toList()), newPassword);
            filePassword.close();
            filePassword = newPassword;

        } catch (IOException | DecryptionFailed e) {
            throw new PasswordNotChanged(e);
        }
    }

    @Override
    public void close() {

        filePassword.close();
    }
}
