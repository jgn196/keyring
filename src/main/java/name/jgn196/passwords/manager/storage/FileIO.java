package name.jgn196.passwords.manager.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public interface FileIO {

    boolean fileExists(File file);
    byte[] readAllFrom(File file) throws IOException;
    File createTempFile() throws IOException;
    void writeAllTo(File file, byte[] data) throws IOException;
    void move(File source, File destination) throws IOException;

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
        public File createTempFile() throws IOException {

            File result = Files.createTempFile("kr", ".dat").toFile();
            result.deleteOnExit();
            return result;
        }

        @Override
        public void writeAllTo(final File file, final byte[] data) throws IOException {

            Files.write(file.toPath(), data);
        }

        @Override
        public void move(File source, File destination) throws IOException {

            Files.move(source.toPath(), destination.toPath(), ATOMIC_MOVE, REPLACE_EXISTING);
        }
    }
}
