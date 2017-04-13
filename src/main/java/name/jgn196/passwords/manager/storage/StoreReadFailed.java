package name.jgn196.passwords.manager.storage;

import java.io.IOException;

class StoreReadFailed extends RuntimeException {

    StoreReadFailed(IOException e) {
        super(e);
    }
}
