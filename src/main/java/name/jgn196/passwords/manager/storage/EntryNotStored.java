package name.jgn196.passwords.manager.storage;

import java.io.IOException;

class EntryNotStored extends RuntimeException {

    EntryNotStored(final IOException cause) {

        super("Failed to store entry.", cause);
    }
}
