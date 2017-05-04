package name.jgn196.passwords.manager.crypto;

import java.security.SecureRandom;
import java.util.function.Supplier;

import static name.jgn196.passwords.manager.crypto.Salt.SALT_SIZE;

public class SaltGenerator implements Supplier<Salt> {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public Salt get() {

        final byte[] result = new byte[SALT_SIZE];
        SECURE_RANDOM.nextBytes(result);

        return new Salt(result);
    }
}
