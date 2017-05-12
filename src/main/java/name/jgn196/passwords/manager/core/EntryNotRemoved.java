package name.jgn196.passwords.manager.core;

import java.io.IOException;

class EntryNotRemoved extends RuntimeException {

    EntryNotRemoved(final IOException cause) {

        super("Failed to remove entry.", cause);
    }
}
