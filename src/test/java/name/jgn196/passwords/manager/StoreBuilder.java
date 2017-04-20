package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.FileStore;

import java.io.File;
import java.io.IOException;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class StoreBuilder {

    private final String storePassword;

    StoreBuilder(final String storePassword) {

        this.storePassword = storePassword;
    }

    void containing(final Login login, final String password) throws IOException {

        new Safe(new FileStore(new File(STORE_FILE_NAME), Password.from(storePassword)))
                .store(login, Password.from(password));
    }
}
