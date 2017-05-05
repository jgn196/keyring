package name.jgn196.passwords.manager.crypto;

import org.bouncycastle.crypto.InvalidCipherTextException;

public class DecryptionFailed extends RuntimeException {

    DecryptionFailed(InvalidCipherTextException e) {

        super(e);
    }

    public DecryptionFailed(final String message) {

        super(message);
    }
}
