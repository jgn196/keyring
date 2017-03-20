package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class ASafe {

    @Test
    public void storesLoginPasswords() throws Exception {

        final SecureStore store = mock(SecureStore.class);
        final Login login = new Login("www.site.net", "Bill");
        final Password password = Password.from("super_secret");

        try (final Safe safe = new Safe(store)) {

            safe.store(login, password);
        }

        verify(store).store(new StoreEntry(login, password));
    }

    @Test
    public void retrievesLoginPassword() throws Exception {

        final SecureStore store = mock(SecureStore.class);
        when(store.stream())
                .thenReturn(Stream.of(
                        new StoreEntry(new Login("ignore me", "Bill"), Password.from("incorrect")),
                        new StoreEntry(new Login("www.site.net", "ignore me"), Password.from("incorrect")),
                        new StoreEntry(new Login("www.site.net", "Bill"), Password.from("super_secret"))));

        try (final Safe safe = new Safe(store)) {

            assertEquals(
                    Optional.of(Password.from("super_secret")),
                    safe.passwordFor(new Login("www.site.net", "Bill")));
        }
    }

    @Test
    public void safelyRetrievesNothingWhenStoreEmpty() throws Exception {

        final SecureStore store = mock(SecureStore.class);
        when(store.stream()).thenReturn(Stream.empty());

        try (final Safe safe = new Safe(store)) {

            assertFalse(safe.passwordFor(new Login("www.site.net", "Bill")).isPresent());
        }
    }

    @Test
    public void safelyRetrievesNothingWhenNoMatchingEntriesInStore() throws Exception {

        final SecureStore store = mock(SecureStore.class);
        when(store.stream())
                .thenReturn(Stream.of(new StoreEntry(new Login("ignore me", "ignore me"), Password.from("incorrect"))));

        try (final Safe safe = new Safe(store)) {

            assertFalse(safe.passwordFor(new Login("www.site.net", "Bill")).isPresent());
        }
    }

    @Test
    public void closesItsStore() throws Exception {

        final SecureStore store = mock(SecureStore.class);

        new Safe(store).close();

        verify(store).close();
    }
}
