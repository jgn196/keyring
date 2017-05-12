package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;
import name.jgn196.passwords.manager.crypto.Salt;
import name.jgn196.passwords.manager.crypto.SaltGenerator;
import name.jgn196.passwords.manager.crypto.StoreEncryption;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AFileStore {

    private static final File TEST_FILE = new File("path");
    private static final byte[] FILE_DATA = "file data".getBytes();
    private static final byte[] CIPHER_TEXT = "cipher text".getBytes();
    private static final Salt SALT = new SaltGenerator().get();
    private static final byte[] PLAIN_TEXT = "plain text".getBytes();
    private static final Crc32 CRC = Crc32.of(PLAIN_TEXT);
    private static final Password PASSWORD = Password.from("secret");
    private static final StoreEntry STORE_ENTRY = new StoreEntry(new Login("www.site.com", "Bill"), PASSWORD);

    private final StoreFormat format = mock(StoreFormat.class);
    private final FileIO fileIO = mock(FileIO.class);
    private final StoreEncryption encryption = mock(StoreEncryption.class);
    private final FileStore fileStore = new FileStoreBuilder()
            .with(TEST_FILE)
            .with(format)
            .with(encryption)
            .with(fileIO)
            .with(PASSWORD)
            .build();

    @Before
    public void setUp() throws IOException {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(true);
        when(fileIO.readAllFrom(TEST_FILE)).thenReturn(FILE_DATA);
        when(format.deserialiseOuterLayer(FILE_DATA))
                .thenReturn(new StoreFormat.DeserialiseResult(SALT, CRC, CIPHER_TEXT));
        when(format.deserialiseEntries(PLAIN_TEXT)).thenReturn(singletonList(STORE_ENTRY));
        when(format.serialise(any(Collection.class))).thenReturn(PLAIN_TEXT);
        when(format.serialise(any(Salt.class), eq(CRC), eq(CIPHER_TEXT))).thenReturn(FILE_DATA);
        when(encryption.decryptWithSalt(CIPHER_TEXT, SALT, PASSWORD)).thenReturn(PLAIN_TEXT);
        when(encryption.encryptWithSalt(eq(PLAIN_TEXT), any(Salt.class), eq(PASSWORD))).thenReturn(CIPHER_TEXT);
    }

    @Test
    public void streamsNothingIfStoreFileMissing() {

        givenNoStoreFile();

        final Stream<StoreEntry> results = fileStore.stream();

        assertEquals(0L, results.count());
    }

    @Test
    public void readsFile() throws IOException {

        fileStore.stream();

        verify(fileIO).readAllFrom(TEST_FILE);
    }

    @Test
    public void deserialisesOuterFormatLayer() throws IOException {

        fileStore.stream();

        verify(format).deserialiseOuterLayer(FILE_DATA);
    }

    @Test
    public void decryptsCipherText() throws IOException {

        fileStore.stream();

        verify(encryption).decryptWithSalt(CIPHER_TEXT, SALT, PASSWORD);
    }

    @Test(expected = DecryptionFailed.class)
    public void failsIfPlainTextCrcIsWrong() throws IOException {

        givenWrongCrc();

        fileStore.stream();
    }

    @Test
    public void deserialisesEntries() throws IOException {

        final Stream<StoreEntry> results = fileStore.stream();

        assertThat(results.collect(toList()), contains(STORE_ENTRY));
        verify(format).deserialiseEntries(PLAIN_TEXT);
    }

    @Test(expected = StoreReadFailed.class)
    public void translatesIoErrorToStoreReadFailure() throws IOException {

        givenFileReadThrows();

        fileStore.stream();
    }

    @Test
    public void serialisesEntries() throws IOException {

        givenNoStoreFile();

        fileStore.store(STORE_ENTRY);

        //noinspection unchecked
        verify(format).serialise((Collection) argThat(contains(STORE_ENTRY)));
    }

    @Test
    public void encryptsPlainText() throws IOException {

        fileStore.store(STORE_ENTRY);

        verify(encryption).encryptWithSalt(eq(PLAIN_TEXT), any(Salt.class), eq(PASSWORD));
    }

    @Test
    public void serialisesOuterFormatLayer() throws IOException {

        fileStore.store(STORE_ENTRY);

        verify(format).serialise(any(Salt.class), eq(CRC), eq(CIPHER_TEXT));
    }

    @Test
    public void writesToFile() throws IOException {

        fileStore.store(STORE_ENTRY);

        verify(fileIO).writeAllTo(TEST_FILE, FILE_DATA);
    }

    @Test(expected = EntryNotStored.class)
    public void translatesIoErrorToEntryNotStoredFailure() throws IOException {

        givenFileWriteThrows();

        fileStore.store(STORE_ENTRY);
    }

    @Test
    public void closesItsEncryption() throws Exception {

        fileStore.close();
    }

    private void givenNoStoreFile() {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(false);
    }

    private void givenWrongCrc() throws IOException {

        when(format.deserialiseOuterLayer(FILE_DATA))
                .thenReturn(new StoreFormat.DeserialiseResult(SALT, new Crc32(CRC.toLong() + 1), CIPHER_TEXT));
    }

    private void givenFileReadThrows() throws IOException {

        when(fileIO.readAllFrom(TEST_FILE)).thenThrow(new IOException());
    }

    private void givenFileWriteThrows() throws IOException {

        doThrow(new IOException()).when(fileIO).writeAllTo(TEST_FILE, FILE_DATA);
    }
}
