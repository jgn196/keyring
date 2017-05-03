package name.jgn196.passwords.manager.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import static java.util.Arrays.copyOf;

class Salt {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    static final int SALT_SIZE = 8;

    private final byte[] data;

    static Salt readSaltFrom(final InputStream in) throws IOException {

        final byte[] data = new byte[SALT_SIZE];
        if (in.read(data) != SALT_SIZE)
            throw new IOException("Failed to read SALT from in memory stream.");

        return new Salt(data);
    }

    private Salt(final byte[] data) {

        assert data.length == SALT_SIZE;

        this.data = data;
    }

    Salt() {

        data = generatedSalt();
    }

    private static byte[] generatedSalt() {

        final byte[] result = new byte[SALT_SIZE];
        SECURE_RANDOM.nextBytes(result);

        return result;
    }

    void writeTo(final OutputStream out) throws IOException {

        out.write(data);
    }

    byte[] toBytes() {

        return copyOf(data, data.length);
    }
}
