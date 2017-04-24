package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;

import java.util.Optional;
import java.util.stream.Collectors;

import static name.jgn196.passwords.manager.Manager.STORE_FILE;

class PostConditions {

    static Iterable<Login> storedLogins(final String storePassword) {
        try (final Safe safe = new Safe(STORE_FILE.openWithPassword(Password.from(storePassword)))) {

            return safe.logins()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Optional<Password> storedPassword(final Login login, final String storePassword) {
        try (final Safe safe = new Safe(STORE_FILE.openWithPassword(Password.from(storePassword)))) {

            return safe.passwordFor(login);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
