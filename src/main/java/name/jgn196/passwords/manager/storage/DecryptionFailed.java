package name.jgn196.passwords.manager.storage;

import org.bouncycastle.crypto.InvalidCipherTextException;

public class DecryptionFailed extends RuntimeException {

    DecryptionFailed(InvalidCipherTextException e) {

        super(e);
    }

    DecryptionFailed(final String message) {

        super(message);
    }
}
