package name.jgn196.passwords.manager.storage;

import java.io.*;
import java.util.zip.CRC32;

final class Crc32 {

    private static final CRC32 CRC_32 = new CRC32();

    private final long value;

    static Crc32 of(final byte[] data) {

        CRC_32.reset();
        CRC_32.update(data, 0, data.length);

        return new Crc32(CRC_32.getValue());
    }

    private Crc32(final long value) {

        this.value = value;
    }

    long toLong() {

        return value;
    }

    void writeTo(final OutputStream out) throws IOException {
        try (final DataOutputStream dataStream = new DataOutputStream(out)) {

            dataStream.writeInt((int) value);
        }
    }

    static Crc32 readFrom(final InputStream in) throws IOException {
        try (DataInputStream dataStream = new DataInputStream(in)) {

            return new Crc32(Integer.toUnsignedLong(dataStream.readInt()));
        }
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Crc32)) return false;

        final Crc32 other = (Crc32) obj;
        return value == other.value;
    }

    @Override
    public int hashCode() {

        return (int) value;
    }

    @Override
    public String toString() {

        return "CRC32: " + Long.toHexString(value);
    }
}
