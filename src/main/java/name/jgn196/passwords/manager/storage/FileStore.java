package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

class FileStore extends SecureStore {

    private final File file;
    private final Password filePassword;

    FileStore(final File file, final Password filePassword) {

        this.file = file;
        this.filePassword = filePassword;
    }

    @Override
    public void store(final StoreEntry entry) {
        try {

            final Set<StoreEntry> entries = storeContents();
            entries.add(entry);
            save(entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<StoreEntry> storeContents() throws IOException {

        if (!file.exists()) return new HashSet<>();

        try (final DataInputStream in = new DataInputStream(new FileInputStream(file))) {

            final int entryCount = in.readInt();
            final HashSet<StoreEntry> results = new HashSet<>();
            for (int i = 0; i < entryCount; i++) {

                final String secureSystem = in.readUTF();
                final String userName = in.readUTF();
                final int passwordLength = in.readInt();
                final char[] password = new char[passwordLength];
                for (int j = 0; j < passwordLength; j++) {

                    password[j] = in.readChar();
                }
                results.add(new StoreEntry(new Login(secureSystem, userName), new Password(password)));
            }

            return results;
        }
    }

    private void save(final Set<StoreEntry> entries) throws IOException {

        try (final DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {

            out.writeInt(entries.size());
            for (final StoreEntry entry : entries) {

                out.writeUTF(entry.login().secureSystem());
                out.writeUTF(entry.login().userName());
                out.writeInt(entry.password().characters().length);
                for (final char c : entry.password().characters()) {

                    out.writeChar(c);
                }
            }
        }
    }

    @Override
    public Stream<StoreEntry> stream() {
        try {

            return storeContents().stream();
        } catch (IOException e) {

            e.printStackTrace();
            return Stream.empty();
        }
    }

    @Override
    public void close() throws Exception {

        // TODO - Wipe password
    }
}
