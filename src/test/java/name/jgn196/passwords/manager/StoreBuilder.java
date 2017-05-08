package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;

import java.io.IOException;

import static name.jgn196.passwords.manager.Manager.STORE_FILE;

class StoreBuilder {

    private final String storePassword;

    StoreBuilder(final String storePassword) {

        this.storePassword = storePassword;
    }

    void containing(final Login login, final String password) throws IOException {
        try (final Safe safe = new Safe(STORE_FILE, Password.from(storePassword))) {

            safe.store(login, Password.from(password));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
