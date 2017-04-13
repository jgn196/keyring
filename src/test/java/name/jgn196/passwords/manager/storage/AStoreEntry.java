package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class AStoreEntry {

    @Test
    public void holdsALogin() {

        final Login login = new Login("system", "user");
        final StoreEntry storeEntry = new StoreEntry(login, null);

        assertEquals(login, storeEntry.login());
        assertTrue(storeEntry.isFor(login));
        assertFalse(storeEntry.isFor(null));
        assertFalse(storeEntry.isFor(new Login("different system", "different user")));
    }

    @Test
    public void holdsAPassword() {

        final Password password = Password.from("password");

        assertEquals(password, new StoreEntry(null, password).password());
    }

    @Test
    public void honoursEqualsContract() {

        EqualsVerifier.forClass(StoreEntry.class)
                .verify();
    }

    @Test
    public void includesLoginInToString() {

        final Login login = new Login("system", "user");

        assertThat(new StoreEntry(login, null).toString(), containsString(login.toString()));
    }

    @Test
    public void includesPasswordInToString() {

        final Password password = Password.from("password");

        assertThat(new StoreEntry(null, password).toString(), containsString(password.toString()));
    }
}
