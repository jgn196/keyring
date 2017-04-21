package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;

import java.util.Optional;
import java.util.stream.Collectors;

class PostConditions {

    static Iterable<Login> storedLogins(final String storePassword) {

        return new Safe(StoreFile.openWithPassword(Password.from(storePassword)))
                .logins()
                .collect(Collectors.toList());
    }

    static Optional<Password> storedPassword(final Login login, final String storePassword) {

        return new Safe(StoreFile.openWithPassword(Password.from(storePassword)))
                .passwordFor(login);
    }
}
