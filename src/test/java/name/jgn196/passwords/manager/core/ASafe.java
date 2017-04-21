package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ASafe {

    private final SecureStore store = mock(SecureStore.class);
    private final Safe safe = new Safe(store);

    @Test
    public void storesLoginPasswords() throws Exception {

        final Login login = new Login("www.site.net", "Bill");
        final Password password = Password.from("super_secret");

        safe.store(login, password);

        verify(store).store(new StoreEntry(login, password));
    }

    @Test
    public void retrievesLoginPassword() throws Exception {

        when(store.stream())
                .thenReturn(Stream.of(
                        new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect")),
                        new StoreEntry(new Login("www.site.net", "ignore me"), Password.from("incorrect")),
                        new StoreEntry(new Login("www.site.net", "Bill"), Password.from("super_secret"))));

        assertEquals(
                Optional.of(Password.from("super_secret")),
                safe.passwordFor(new Login("www.site.net", "Bill")));
    }

    @Test
    public void safelyRetrievesNothingWhenStoreEmpty() throws Exception {

        when(store.stream()).thenReturn(Stream.empty());

        assertFalse(safe.passwordFor(new Login("www.site.net", "Bill")).isPresent());
    }

    @Test
    public void safelyRetrievesNothingWhenNoMatchingEntriesInStore() throws Exception {

        when(store.stream())
                .thenReturn(Stream.of(new StoreEntry(new Login("ignore me", "ignore me"), Password.from("incorrect"))));

        assertFalse(safe.passwordFor(new Login("www.site.net", "Bill")).isPresent());
    }

    @Test
    public void listsLogins() throws Exception {

        when(store.stream())
                .thenReturn(Stream.of(
                        new StoreEntry(new Login("site 1", "Bill"), Password.from("ignored")),
                        new StoreEntry(new Login("site 1", "Ted"), Password.from("ignored")),
                        new StoreEntry(new Login("site 2", "Bill"), Password.from("ignored"))));

        assertThat(
                safe.logins().collect(toList()),
                hasItems(
                        new Login("site 1", "Bill"),
                        new Login("site 1", "Ted"),
                        new Login("site 2", "Bill")));
    }

    @Test
    public void safelyRemovesMissingLogin() {

        when(store.stream())
                .thenReturn(Stream.empty());

        assertFalse(safe.remove(new Login("www.site.com", "Bill")));
    }

    @Test
    public void removesMissingLogin() {

        when(store.stream())
                .thenReturn(Stream.of(new StoreEntry(new Login("www.site.com", "Bill"), Password.from("password"))));

        assertTrue(safe.remove(new Login("www.site.com", "Bill")));
        verify(store).remove(new StoreEntry(new Login("www.site.com", "Bill"), Password.from("password")));
    }
}
