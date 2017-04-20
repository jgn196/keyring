package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.FileStore;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class PostConditions {

    static Iterable<Login> storedLogins(final String storePassword) {

        return new Safe(new FileStore(new File(STORE_FILE_NAME), Password.from(storePassword)))
                .logins()
                .collect(Collectors.toList());
    }

    static Optional<Password> storedPassword(final Login login, final String storePassword) {

        return new Safe(new FileStore(new File(STORE_FILE_NAME), Password.from(storePassword)))
                .passwordFor(login);
    }
}
