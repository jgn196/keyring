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

import static java.util.Collections.singleton;
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
    public void streamsNothingIfStoreFileMissing() throws IOException {

        givenNoStoreFile();

        final Stream<StoreEntry> results = fileStore.readEntriesUsing(PASSWORD);

        assertEquals(0L, results.count());
    }

    @Test
    public void readsFile() throws IOException {

        fileStore.readEntriesUsing(PASSWORD);

        verify(fileIO).readAllFrom(TEST_FILE);
    }

    @Test
    public void deserialisesOuterFormatLayer() throws IOException {

        fileStore.readEntriesUsing(PASSWORD);

        verify(format).deserialiseOuterLayer(FILE_DATA);
    }

    @Test
    public void decryptsCipherText() throws IOException {

        fileStore.readEntriesUsing(PASSWORD);

        verify(encryption).decryptWithSalt(CIPHER_TEXT, SALT, PASSWORD);
    }

    @Test(expected = DecryptionFailed.class)
    public void failsIfPlainTextCrcIsWrong() throws IOException {

        givenWrongCrc();

        fileStore.readEntriesUsing(PASSWORD);
    }

    @Test
    public void deserialisesEntries() throws IOException {

        final Stream<StoreEntry> results = fileStore.readEntriesUsing(PASSWORD);

        assertThat(results.collect(toList()), contains(STORE_ENTRY));
        verify(format).deserialiseEntries(PLAIN_TEXT);
    }

    @Test
    public void serialisesEntries() throws IOException {

        givenNoStoreFile();

        fileStore.writeEntries(singleton(STORE_ENTRY), PASSWORD);

        //noinspection unchecked
        verify(format).serialise((Collection) argThat(contains(STORE_ENTRY)));
    }

    @Test
    public void encryptsPlainText() throws IOException {

        fileStore.writeEntries(singleton(STORE_ENTRY), PASSWORD);

        verify(encryption).encryptWithSalt(eq(PLAIN_TEXT), any(Salt.class), eq(PASSWORD));
    }

    @Test
    public void serialisesOuterFormatLayer() throws IOException {

        fileStore.writeEntries(singleton(STORE_ENTRY), PASSWORD);

        verify(format).serialise(any(Salt.class), eq(CRC), eq(CIPHER_TEXT));
    }

    @Test
    public void writesToFile() throws IOException {

        final File tempFile = new File("temp");
        when(fileIO.createTempFile()).thenReturn(tempFile);

        fileStore.writeEntries(singleton(STORE_ENTRY), PASSWORD);

        verify(fileIO).createTempFile();
        verify(fileIO).writeAllTo(tempFile, FILE_DATA);
        verify(fileIO).move(tempFile, TEST_FILE);
    }

    private void givenNoStoreFile() {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(false);
    }

    private void givenWrongCrc() throws IOException {

        when(format.deserialiseOuterLayer(FILE_DATA))
                .thenReturn(new StoreFormat.DeserialiseResult(SALT, new Crc32(CRC.toLong() + 1), CIPHER_TEXT));
    }
}
