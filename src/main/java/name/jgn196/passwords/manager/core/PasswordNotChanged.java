package name.jgn196.passwords.manager.core;

public class PasswordNotChanged extends RuntimeException {

    PasswordNotChanged(final Exception cause) {

        super("Store password not changed.", cause);
    }
}
