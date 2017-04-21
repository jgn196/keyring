package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;

import java.io.IOException;

class StoreBuilder {

    private final String storePassword;

    StoreBuilder(final String storePassword) {

        this.storePassword = storePassword;
    }

    void containing(final Login login, final String password) throws IOException {

        new Safe(StoreFile.openWithPassword(Password.from(storePassword)))
                .store(login, Password.from(password));
    }
}
