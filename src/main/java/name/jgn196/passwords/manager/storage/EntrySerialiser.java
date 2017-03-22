package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class EntrySerialiser {

    private final DataOutputStream stream;

    EntrySerialiser(final DataOutputStream stream) {

        this.stream = stream;
    }

    void serialise(final StoreEntry entry) throws IOException {

        stream.writeUTF(entry.login().secureSystem());
        stream.writeUTF(entry.login().userName());
        stream.writeInt(entry.password().characters().length);
        for (final char c : entry.password().characters()) {

            stream.writeChar(c);
        }
    }
}