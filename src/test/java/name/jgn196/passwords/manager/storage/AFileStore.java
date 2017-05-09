package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;
import name.jgn196.passwords.manager.crypto.Salt;
import name.jgn196.passwords.manager.crypto.SaltGenerator;
import name.jgn196.passwords.manager.crypto.StoreEncryption;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AFileStore {

    private static final File TEST_FILE = new File("path");

    private final StoreFormat format = mock(StoreFormat.class);
    private final FileIO fileIO = mock(FileIO.class);
    private final StoreEncryption encryption = mock(StoreEncryption.class);
    private final FileStore fileStore = new FileStoreBuilder()
            .with(TEST_FILE)
            .with(format)
            .with(encryption)
            .with(fileIO)
            .build();

    @Test
    public void streamsNothingIfStoreFileMissing() {

        givenNoStoreFile();

        assertEquals(0L, fileStore.stream().count());
    }

    @Test
    public void readsFile() throws IOException {

        givenStoreFileExists();
        ignoreOuterLayerDeserialisation();
        ignoreDecryption();

        fileStore.stream();

        verify(fileIO).readAllFrom(TEST_FILE);
    }

    @Test
    public void deserialisesOuterFormatLayer() throws IOException {

        final byte[] fileData = new byte[0];
        givenStoreFileContains(fileData);
        ignoreOuterLayerDeserialisation();
        ignoreDecryption();

        fileStore.stream();

        verify(format).deserialiseOuterLayer(fileData);
    }

    @Test
    public void decryptsCipherText() throws IOException {

        final byte[] cipherText = new byte[0];
        final Salt salt = new SaltGenerator().get();
        givenStoreFileExists();
        givenOuterFormatContains(cipherText, salt);
        ignoreDecryption();

        fileStore.stream();

        verify(encryption).decryptWithSalt(cipherText, salt);
    }

    @Test(expected = DecryptionFailed.class)
    public void failsIfPlainTextCrcIsWrong() throws IOException {

        final byte[] pl = new byte[0];
        givenStoreFileExists();
        givenOuterFormatContains(notCrcOf(pl));
        givenFileContentDecryptsTo(pl);

        fileStore.stream();
    }

    @Test
    public void deserialisesEntries() throws IOException {

        final byte[] pl = new byte[0];
        final StoreEntry entry = new StoreEntry(new Login("www.site.com", "Bill"), Password.from("another secret"));
        givenStoreFileExists();
        ignoreOuterLayerDeserialisation();
        givenFileContentDecryptsTo(pl);
        givenPlDeserialisesTo(entry);

        assertThat(fileStore.stream().collect(toList()), contains(entry));

        verify(format).deserialiseEntries(pl);
    }

    @Test(expected = StoreReadFailed.class)
    public void translatesIoErrorToStoreReadFailure() throws IOException {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(true);
        when(fileIO.readAllFrom(TEST_FILE)).thenThrow(new IOException());

        fileStore.stream();
    }

    private void givenNoStoreFile() {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(false);
    }

    private void givenStoreFileExists() throws IOException {

        givenStoreFileContains(new byte[0]);
    }

    private void givenStoreFileContains(final byte[] value) throws IOException {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(true);
        when(fileIO.readAllFrom(TEST_FILE)).thenReturn(value);
    }

    private void ignoreOuterLayerDeserialisation() throws IOException {

        givenOuterFormatContains(null, null);
    }

    private void givenOuterFormatContains(final byte[] cipherText, final Salt salt) throws IOException {

        when(format.deserialiseOuterLayer(any(byte[].class)))
                .thenReturn(new StoreFormat.DeserialiseResult(salt, new Crc32(0L), cipherText));
    }

    private void givenOuterFormatContains(final Crc32 crc) throws IOException {

        when(format.deserialiseOuterLayer(any(byte[].class)))
                .thenReturn(new StoreFormat.DeserialiseResult(null, crc, null));
    }

    private void ignoreDecryption() {

        givenFileContentDecryptsTo(new byte[0]);
    }

    private void givenFileContentDecryptsTo(final byte[] pl) {

        when(encryption.decryptWithSalt(any(byte[].class), any(Salt.class))).thenReturn(pl);
    }

    private Crc32 notCrcOf(final byte[] data) {

        return new Crc32(Crc32.of(data).toLong() + 1L);
    }

    private void givenPlDeserialisesTo(final StoreEntry entry) throws IOException {

        when(format.deserialiseEntries(any(byte[].class))).thenReturn(singletonList(entry));
    }

    private byte[] writtenData() throws IOException {

        final ArgumentCaptor<byte[]> dataCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(fileIO).writeAllTo(eq(TEST_FILE), dataCaptor.capture());
        return dataCaptor.getValue();
    }
}
