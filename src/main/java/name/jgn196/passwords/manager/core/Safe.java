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
        try(final StoreEntries readEntries = readEntries()) {

            final Map<Login, StoreEntry> entries = readEntries.stream().collect(toMap(StoreEntry::login, e -> e));

            entries.put(login, new StoreEntry(login, password));

            writeEntries(entries.values());

        } catch (IOException e) {
            throw new EntryNotStored(e);
        }
    }

    private StoreEntries readEntries() throws IOException {

        return new StoreEntries(store.readEntriesUsing(filePassword));
    }

    private static class StoreEntries implements AutoCloseable {

        private final List<StoreEntry> entries;

        StoreEntries(final Stream<StoreEntry> entries) {

            this.entries = entries.collect(toList());
        }

        Stream<StoreEntry> stream(){

            return entries.stream();
        }

        @Override
        public void close() {

            entries.forEach(StoreEntry::close);
        }
    }

    private void writeEntries(final Collection<StoreEntry> values) throws IOException {

        store.writeEntries(values, filePassword);
    }

    public Optional<Password> passwordFor(final Login login) {
        try(final StoreEntries readEntries = readEntries()) {

            final Optional<Password> password = readEntries
                    .stream()
                    .filter(entry -> entry.isFor(login))
                    .map(StoreEntry::password)
                    .findFirst();

            return password.map(Password::copy);

        } catch (IOException e) {
            throw new EntriesNotRead(e);
        }
    }

    public Stream<Login> logins() {
        try(final StoreEntries readEntries = readEntries()) {

            return readEntries.stream().map(StoreEntry::login);

        } catch (IOException e) {
            throw new EntriesNotRead(e);
        }
    }

    public boolean remove(final Login login) {
        try(final StoreEntries readEntries = readEntries()) {

            final Collection<StoreEntry> updatedEntries = readEntries.stream().collect(toList());
            final boolean entriesRemoved = updatedEntries.removeIf(e -> e.isFor(login));

            if (entriesRemoved) writeEntries(updatedEntries);

            return entriesRemoved;

        } catch (IOException e) {
            throw new EntryNotRemoved(e);
        }
    }

    public void changePasswordTo(final Password newPassword) {
        if (newPassword.equals(filePassword)) return;

        try(final StoreEntries readEntries = readEntries()) {

            filePassword.close();
            filePassword = newPassword;

            writeEntries(readEntries.stream().collect(toList()));

        } catch (IOException | DecryptionFailed e) {
            throw new PasswordNotChanged(e);
        }
    }

    @Override
    public void close() {

        filePassword.close();
    }
}
