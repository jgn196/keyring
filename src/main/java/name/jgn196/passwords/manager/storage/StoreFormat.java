package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;

class StoreFormat {

    static final int VERSION = 1;

    byte[] serialise(final Collection<StoreEntry> entries) throws IOException {

        try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final DataOutputStream out = new DataOutputStream(buffer)) {

            out.writeInt(VERSION);
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

    Collection<StoreEntry> deserialise(final byte[] formatted) throws IOException {

        try (final DataInputStream in = new DataInputStream(new ByteArrayInputStream(formatted))) {

            readAndCheckVersion(in);

            final int entryCount = in.readInt();
            final HashSet<StoreEntry> results = new HashSet<>();
            for (int i = 0; i < entryCount; i++) {

                results.add(deserialise(in));
            }

            return results;
        }
    }

    private void readAndCheckVersion(final DataInputStream in) throws IOException {

        final int version = in.readInt();
        if (version != VERSION)
            throw new UnsupportedStoreFormat(version);
    }

    private StoreEntry deserialise(final DataInputStream in) throws IOException {

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
