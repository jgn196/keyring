package name.jgn196.passwords.manager.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public interface FileIO {

    boolean fileExists(File file);
    byte[] readAllFrom(File file) throws IOException;
    void writeAllTo(File file, byte[] data) throws IOException;

    class Implementation implements FileIO {

        @Override
        public boolean fileExists(final File file) {

            return file.exists();
        }

        @Override
        public byte[] readAllFrom(final File file) throws IOException {

            return Files.readAllBytes(file.toPath());
        }

        @Override
        public void writeAllTo(final File file, final byte[] data) throws IOException {

            Files.write(file.toPath(), data);
        }
    }
}
