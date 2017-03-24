package name.jgn196.passwords.manager.storage;

import javax.crypto.BadPaddingException;

public class DecryptionFailed extends RuntimeException {

    DecryptionFailed(BadPaddingException e) {

        super(e);
    }
}
