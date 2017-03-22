package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

import java.io.DataInputStream;
import java.io.IOException;

class EntryDeserialiser {

    private final DataInputStream stream;

    EntryDeserialiser(final DataInputStream stream) {

        this.stream = stream;
    }

    StoreEntry deserialise() throws IOException {

        final String secureSystem = stream.readUTF();
        final String userName = stream.readUTF();
        final int passwordLength = stream.readInt();
        final char[] password = new char[passwordLength];
        for (int i = 0; i < passwordLength; i++) {

            password[i] = stream.readChar();
        }
        return new StoreEntry(new Login(secureSystem, userName), new Password(password));
    }
}
