package name.jgn196.passwords.manager.crypto;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static name.jgn196.passwords.manager.crypto.Salt.SALT_SIZE;
import static org.junit.Assert.assertTrue;

public class ASalt {

    @Test
    public void canBeGenerated() {

        final Salt salt = new Salt();

        assertTrue(isSalt(salt.toBytes()));
    }

    @Test
    public void writesToStream() throws IOException {

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            new Salt().writeTo(out);

            assertTrue(isSalt(out.toByteArray()));
        }
    }

    @Test
    public void readsFromStream() throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(new Salt().toBytes())) {

            assertTrue(isSalt(Salt.readSaltFrom(in).toBytes()));
        }
    }

    @Test(expected = IOException.class)
    public void throwsIfSaltTruncated() throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(new byte[SALT_SIZE / 2])) {

            Salt.readSaltFrom(in);
        }
    }

    private static boolean isSalt(final byte[] data) {

        return data.length == SALT_SIZE && !isAllZero(data);
    }

    private static boolean isAllZero(final byte[] data) {

        for (final byte d : data) {

            if (d != 0) return false;
        }

        return true;
    }
}
