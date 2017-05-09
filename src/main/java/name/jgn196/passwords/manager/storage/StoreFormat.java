package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.crypto.Salt;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;

public class StoreFormat {

    static final int VERSION = 1;

    byte[] serialise(final Collection<StoreEntry> entries) throws IOException {

        try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final DataOutputStream out = new DataOutputStream(buffer)) {

            out.writeInt(entries.size());
            for (final StoreEntry entry : entries) {

                serialise(entry, out);
            }

            return buffer.toByteArray();
        }
    }

    private void serialise(final StoreEntry entry, final DataOutputStream out) throws IOException {

        out.writeUTF(entry.login().secureSystem());
        out.writeUTF(entry.login().userName());
        serialise(entry.password(), out);
    }

    private void serialise(final Password password, final DataOutputStream out) throws IOException {

        out.writeInt(password.characters().length);
        for (final char c : password.characters()) {

            out.writeChar(c);
        }
    }

    byte[] serialise(final Salt salt, final Crc32 crc, final byte[] encryptedEntries) throws IOException {
        try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             final DataOutputStream out = new DataOutputStream(buffer)) {

            out.writeInt(VERSION);
            salt.writeTo(out);
            crc.writeTo(out);
            out.writeInt(encryptedEntries.length);
            out.write(encryptedEntries);

            return buffer.toByteArray();
        }
    }

    DeserialiseResult deserialiseOuterLayer(final byte[] formatted) throws IOException {
        try (final DataInputStream in = new DataInputStream(new ByteArrayInputStream(formatted))) {

            readAndCheckVersion(in);

            final Salt salt = Salt.readSaltFrom(in);
            final Crc32 crc = Crc32.readFrom(in);
            final int ctSize = in.readInt();
            final byte[] ct = new byte[ctSize];
            final int read = in.read(ct);

            if (read != ctSize) throw new IOException("ct data truncated");

            return new DeserialiseResult(salt, crc, ct);
        }
    }

    static class DeserialiseResult {

        private final Salt salt;
        private final Crc32 crc;
        private final byte[] ct;

        DeserialiseResult(final Salt salt, final Crc32 crc, final byte[] ct) {

            this.salt = salt;
            this.crc = crc;
            this.ct = ct;
        }

        Salt salt() { return salt; }
        Crc32 crc() { return crc; }
        byte[] ct() { return ct; }
    }

    private void readAndCheckVersion(final DataInputStream in) throws IOException {

        final int version = in.readInt();
        if (version != VERSION)
            throw new UnsupportedStoreFormat(version);
    }

    Collection<StoreEntry> deserialiseEntries(final byte[] formatted) throws IOException {
        try (final DataInputStream in = new DataInputStream(new ByteArrayInputStream(formatted))) {

            final int entryCount = in.readInt();
            final HashSet<StoreEntry> results = new HashSet<>();
            for (int i = 0; i < entryCount; i++) {

                results.add(deserialiseEntries(in));
            }

            return results;
        }
    }

    private StoreEntry deserialiseEntries(final DataInputStream in) throws IOException {

        final String secureSystem = in.readUTF();
        final String userName = in.readUTF();
        final Password password = readPassword(in);
        return new StoreEntry(new Login(secureSystem, userName), password);
    }

    private Password readPassword(final DataInputStream in) throws IOException {

        final int passwordLength = in.readInt();
        final char[] password = new char[passwordLength];
        for (int i = 0; i < passwordLength; i++) {

            password[i] = in.readChar();
        }
        return new Password(password);
    }
}
