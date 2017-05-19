package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.crypto.DecryptionFailed;
import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ASafe {

    private final Password password = Password.from("file password");
    private final SecureStore store = mock(SecureStore.class);
    private final List<StoreEntry> entriesToRead = new ArrayList<>();
    private final Safe safe = new Safe(store, password);

    @Test
    public void storesNewLoginPassword() throws Exception {

        final Login login = new Login("www.site.net", "Bill");
        final Password password = Password.from("super_secret");
        givenStoreContains(new StoreEntry(new Login("ignored", "ignored"), Password.from("ignored")));

        safe.store(login, password);

        verify(store).readEntriesUsing(this.password);
        verify(store).writeEntries(hasEntries(new StoreEntry(login, password)), eq(this.password));
        passwordsReadFromStoreAreClosed();
    }

    @Test
    public void updatesNewLoginPassword() throws Exception {

        final Login login = new Login("www.site.net", "Bill");
        final Password password = Password.from("super_secret");
        givenStoreContains(new StoreEntry(login, Password.from("overwrite me")));

        safe.store(login, password);

        verify(store).readEntriesUsing(this.password);
        verify(store).writeEntries(hasEntries(new StoreEntry(login, password)), eq(this.password));
        passwordsReadFromStoreAreClosed();
    }

    @Test(expected = EntryNotStored.class)
    public void throwsStoreIOFailure() throws Exception {

        givenReadWillFail();

        safe.store(new Login("www.site.net", "Bill"), Password.from("super_secret"));
    }

    @Test
    public void retrievesLoginPassword() throws IOException {

        givenStoreContains(
                new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect")),
                new StoreEntry(new Login("www.site.net", "ignore me"), Password.from("incorrect")),
                new StoreEntry(new Login("www.site.net", "Bill"), Password.from("super_secret")));

        assertEquals(
                Optional.of(Password.from("super_secret")),
                safe.passwordFor(new Login("www.site.net", "Bill")));
        passwordsReadFromStoreAreClosed();
    }

    @Test
    public void retrievesNothingWhenStoreEmpty() throws IOException {

        givenStoreContains();

        assertEquals(Optional.empty(), safe.passwordFor(new Login("www.site.net", "Bill")));
    }

    @Test
    public void retrievesNothingWhenNoMatchingEntriesInStore() throws IOException {

        givenStoreContains(
                new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect")),
                new StoreEntry(new Login("www.site.net", "ignore me"), Password.from("incorrect")),
                new StoreEntry(new Login("www.site.net", "Bill"), Password.from("super_secret")));

        assertEquals(Optional.empty(), safe.passwordFor(new Login("foo", "bar")));
        passwordsReadFromStoreAreClosed();
    }

    @Test(expected = EntriesNotRead.class)
    public void throwsRetrieveIOFailure() throws IOException {

        givenReadWillFail();

        safe.passwordFor(new Login("ignore me", "ignore me"));
    }

    @Test
    public void listsLogins() throws Exception {

        givenStoreContains(
                new StoreEntry(new Login("site 1", "Bill"), Password.from("ignored")),
                new StoreEntry(new Login("site 1", "Ted"), Password.from("ignored")),
                new StoreEntry(new Login("site 2", "Bill"), Password.from("ignored")));

        assertThat(
                safe.logins().collect(toList()),
                hasItems(
                        new Login("site 1", "Bill"),
                        new Login("site 1", "Ted"),
                        new Login("site 2", "Bill")));
        passwordsReadFromStoreAreClosed();
    }

    @Test(expected = EntriesNotRead.class)
    public void throwsListLoginsIOFailure() throws IOException {

        givenReadWillFail();

        safe.logins();
    }

    @Test
    public void removesLogin() throws IOException {

        givenStoreContains(
                new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect")),
                new StoreEntry(new Login("www.site.net", "Bill"), Password.from("super_secret")));
        final List<StoreEntry> writtenEntries = new ArrayList<>();
        doAnswer(invocation -> {
            final Object[] arguments = invocation.getArguments();
            final Collection<StoreEntry> entries = (Collection<StoreEntry>) arguments[0];
            entries.forEach(e -> writtenEntries.add(new StoreEntry(e.login(), e.password().copy())));
            return null;
        })
                .when(store)
                .writeEntries(anyCollection(), any(Password.class));

        assertTrue(safe.remove(new Login("www.site.net", "Bill")));
        verify(store).writeEntries(anyCollection(), eq(password));
        assertThat(writtenEntries, Matchers.hasItems(new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect"))));
        passwordsReadFromStoreAreClosed();
    }

    @Test
    public void safelyRemovesMissingLogin() throws IOException {

        givenStoreIsEmpty();

        assertFalse(safe.remove(new Login("www.site.com", "Bill")));
    }

    @Test(expected = EntryNotRemoved.class)
    public void throwsRemoveLoginIOFailure() throws IOException {

        givenReadWillFail();

        safe.remove(new Login("www.site.com", "Bill"));
    }

    @Test
    public void canChangeStorePassword() throws Exception {

        final Password newPassword = Password.from("new_password");
        givenStoreContains(new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect")));
        final List<StoreEntry> writtenEntries = new ArrayList<>();
        doAnswer(invocation -> {
            final Object[] arguments = invocation.getArguments();
            final Collection<StoreEntry> entries = (Collection<StoreEntry>) arguments[0];
            entries.forEach(e -> writtenEntries.add(new StoreEntry(e.login(), e.password().copy())));
            return null;
        })
                .when(store)
                .writeEntries(anyCollection(), any(Password.class));

        safe.changePasswordTo(newPassword);

        verify(store).readEntriesUsing(password);
        verify(store).writeEntries(anyCollection(), eq(newPassword));
        assertThat(writtenEntries, Matchers.hasItems(new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect"))));
        assertTrue(password.isClosed());
        passwordsReadFromStoreAreClosed();
    }

    @Test
    public void ignoresChangingPasswordToItself() throws IOException {

        safe.changePasswordTo(password);

        verify(store, never()).readEntriesUsing(any(Password.class));
        verify(store, never()).writeEntries(any(Collection.class), any(Password.class));
        assertFalse(password.isClosed());
    }

    @Test(expected = PasswordNotChanged.class)
    public void throwsPasswordChangeIOFailure() throws IOException {

        givenReadWillFail();

        safe.changePasswordTo(Password.from("ignore me"));
    }

    @Test(expected = PasswordNotChanged.class)
    public void throwsPasswordChangeWrongPassword() throws IOException {

        when(store.readEntriesUsing(any(Password.class))).thenThrow(new DecryptionFailed("test"));

        safe.changePasswordTo(Password.from("ignore me"));
    }

    @Test
    public void closesPassword() {

        safe.close();

        assertTrue(password.isClosed());
    }

    private void givenStoreIsEmpty() throws IOException {

        givenStoreContains();
    }

    private void passwordsReadFromStoreAreClosed() {

        entriesToRead.forEach(e -> assertTrue(e.password().isClosed()));
    }

    private void givenStoreContains(final StoreEntry... entries) throws IOException {

        entriesToRead.addAll(Arrays.asList(entries));
        when(store.readEntriesUsing(password)).thenReturn(Stream.of(entries));
    }

    private Collection<StoreEntry> hasEntries(final StoreEntry... entries) {

        //noinspection unchecked
        return (Collection<StoreEntry>) argThat(hasItems(entries));
    }

    private void givenReadWillFail() throws IOException {

        when(store.readEntriesUsing(any(Password.class))).thenThrow(new IOException());
    }

    @After
    public void closeSafe() {

        safe.close();
    }
}
