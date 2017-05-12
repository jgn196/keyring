package name.jgn196.passwords.manager.core;

import java.io.IOException;

class PasswordNotChanged extends RuntimeException {

    PasswordNotChanged(final IOException cause) {

        super("Store password not changed.", cause);
    }
}
