package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.crypto.DecryptionFailed;
import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Safe implements AutoCloseable {

    private final SecureStore store;
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

            final List<StoreEntry> readEntries = readEntries().collect(toList());
            final Map<Login, StoreEntry> entries = readEntries.stream().collect(toMap(StoreEntry::login, e -> e));
            final StoreEntry entry = new StoreEntry(login, password);

            entries.put(login, entry);

            writeEntries(entries.values());
            readEntries.forEach(e -> e.password().close());

        } catch (IOException e) {
            throw new EntryNotStored(e);
        }
    }

    private Stream<StoreEntry> readEntries() throws IOException {

        return store.readEntriesUsing(filePassword);
    }

    private void writeEntries(final Collection<StoreEntry> values) throws IOException {

        store.writeEntries(values, filePassword);
    }

    public Optional<Password> passwordFor(final Login login) {
        try {

            final List<StoreEntry> readEntries = readEntries().collect(toList());
            Optional<Password> password = readEntries
                    .stream()
                    .filter(entry -> entry.isFor(login))
                    .map(StoreEntry::password)
                    .findFirst();
            if (password.isPresent()) password = Optional.of(password.get().copy());
            readEntries.forEach(e -> e.password().close());
            return password;


        } catch (IOException e) {
            throw new EntriesNotRead(e);
        }
    }

    public Stream<Login> logins() {
        try {

            final List<StoreEntry> readEntries = readEntries().collect(toList());
            readEntries.forEach(e -> e.password().close());
            return readEntries.stream().map(StoreEntry::login);

        } catch (IOException e) {
            throw new EntriesNotRead(e);
        }
    }

    public boolean remove(final Login login) {
        try {

            final List<StoreEntry> readEntries = readEntries().collect(toList());
            final Collection<StoreEntry> entries = new ArrayList<>(readEntries);
            final boolean entriesRemoved = entries.removeIf(e -> e.isFor(login));

            if (entriesRemoved) writeEntries(entries);
            readEntries.forEach(e -> e.password().close());

            return entriesRemoved;

        } catch (IOException e) {
            throw new EntryNotRemoved(e);
        }
    }

    public void changePasswordTo(final Password newPassword) {
        try {

            if (newPassword.equals(filePassword)) return;

            final List<StoreEntry> entries = readEntries().collect(toList());

            filePassword.close();
            filePassword = newPassword;

            writeEntries(entries);
            entries.forEach(e -> e.password().close());

        } catch (IOException | DecryptionFailed e) {
            throw new PasswordNotChanged(e);
        }
    }

    @Override
    public void close() {

        filePassword.close();
    }
}
