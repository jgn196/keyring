package name.jgn196.passwords.manager.core;

import java.io.IOException;

class EntriesNotRead extends RuntimeException {

    EntriesNotRead(final IOException cause) {

        super("Failed to read store entries.", cause);
    }
}
