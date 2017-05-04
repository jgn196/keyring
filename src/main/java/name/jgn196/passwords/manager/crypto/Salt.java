package name.jgn196.passwords.manager.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static java.util.Arrays.copyOf;

public final class Salt {

    public static final int SALT_SIZE = 8;

    private final byte[] data;

    public static Salt readSaltFrom(final InputStream in) throws IOException {

        final byte[] data = new byte[SALT_SIZE];
        if (in.read(data) != SALT_SIZE)
            throw new IOException("Failed to read SALT from in memory stream.");

        return new Salt(data);
    }

    Salt(final byte[] data) {

        assert data.length == SALT_SIZE;

        this.data = data;
    }

    public void writeTo(final OutputStream out) throws IOException {

        out.write(data);
    }

    byte[] toBytes() {

        return copyOf(data, data.length);
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Salt)) return false;

        final Salt other = (Salt) obj;
        return Arrays.equals(data, other.data);
    }

    @Override
    public int hashCode() {

        //noinspection ConstantConditions - Required by EqualsVerifier unit test
        if (data == null) return 0;

        int result = 7;
        for (final byte d : data) {

            result = 37 * result + d;
        }

        return result;
    }
}

