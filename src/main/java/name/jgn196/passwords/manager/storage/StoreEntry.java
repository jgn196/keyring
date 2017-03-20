package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

public class StoreEntry {

    private final Login login;
    private final Password password;

    public StoreEntry(final Login login, final Password password) {

        this.login = login;
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof StoreEntry)) return false;

        final StoreEntry other = (StoreEntry) obj;
        return login.equals(other.login) && password.equals(other.password);
    }
}
