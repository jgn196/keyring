package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

final public class StoreEntry implements AutoCloseable {

    private final Login login;
    private final Password password;

    public StoreEntry(final Login login, final Password password) {

        this.login = login;
        this.password = password;
    }

    public boolean isFor(final Login login) {

        return this.login.equals(login);
    }

    public Login login() {

        return login;
    }

    public Password password() {

        return password;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof StoreEntry)) return false;

        final StoreEntry other = (StoreEntry) obj;
        return (login == null ? other.login == null : login.equals(other.login)) &&
                (password == null ? other.password == null : password.equals(other.password));
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + (login == null ? 0 : login.hashCode());
        result = 31 * result + (password == null ? 0 : password.hashCode());

        return result;
    }

    @Override
    public String toString() {

        return "StoreEntry (" + login + ", " + password + ")";
    }

    @Override
    public void close() {

        password.close();
    }
}