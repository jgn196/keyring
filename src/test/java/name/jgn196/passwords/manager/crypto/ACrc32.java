package name.jgn196.passwords.manager.crypto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.lang.Long.toHexString;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class ACrc32 {

    private static final byte[] KNOWN_INPUT = "test".getBytes();
    private static final long KNOWN_OUTPUT = 3632233996L;
    private static final byte[] KNOWN_OUTPUT_BYTES = {(byte) 0xD8, 0x7F, 0x7E, 0x0C};

    @Test
    public void canBeGenerated() {

        assertEquals(KNOWN_OUTPUT, Crc32.of(KNOWN_INPUT).toLong());
    }

    @Test
    public void writesToStream() throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Crc32.of(KNOWN_INPUT).writeTo(out);

            assertArrayEquals(KNOWN_OUTPUT_BYTES, out.toByteArray());
        }
    }

    @Test
    public void canBeReadFromStream() throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(KNOWN_OUTPUT_BYTES)) {

            assertEquals(KNOWN_OUTPUT, Crc32.readFrom(in).toLong());
        }
    }

    @Test(expected = IOException.class)
    public void throwsIfCrcTruncated() throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(new byte[2])) {

            Crc32.readFrom(in);
        }
    }

    @Test
    public void honoursTheEqualsContract() {

        EqualsVerifier.forClass(Crc32.class)
                .verify();
    }

    @Test
    public void includeLongValueInToString() {

        assertThat(Crc32.of(KNOWN_INPUT).toString(), containsString(toHexString(KNOWN_OUTPUT)));
    }
}
