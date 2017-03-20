package name.jgn196.passwords.manager.core;

import name.jgn196.passwords.manager.storage.SecureStore;
import name.jgn196.passwords.manager.storage.StoreEntry;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
}
