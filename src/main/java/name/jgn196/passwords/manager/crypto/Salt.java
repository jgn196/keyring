package name.jgn196.passwords.manager.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.function.Supplier;

import static java.util.Arrays.copyOf;
import static name.jgn196.passwords.manager.crypto.Salt.SALT_SIZE;

class Salt {

    static final int SALT_SIZE = 8;

    private final byte[] data;

    static Salt readSaltFrom(final InputStream in) throws IOException {

        final byte[] data = new byte[SALT_SIZE];
        if (in.read(data) != SALT_SIZE)
            throw new IOException("Failed to read SALT from in memory stream.");

        return new Salt(data);
    }

    Salt(final byte[] data) {

        assert data.length == SALT_SIZE;

        this.data = data;
    }

    void writeTo(final OutputStream out) throws IOException {

        out.write(data);
    }

    byte[] toBytes() {

        return copyOf(data, data.length);
    }
}

