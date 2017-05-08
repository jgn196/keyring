package name.jgn196.passwords.manager.storage;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AFileStore {

    private static final File TEST_FILE = new File("path");
    private static final Password STORE_PASSWORD = Password.from("secret");


    private final FileIO fileIO = mock(FileIO.class);
    private final FileStore fileStore = new FileStore(TEST_FILE, STORE_PASSWORD);

    @Before
    public void injectFileMockIO() {

        fileStore.inject(fileIO);
    }

    @Test
    public void streamsNothingIfStoreFileMissing() {

        givenNoStoreFile();

        assertEquals(0L, fileStore.stream().count());
    }

    @Test
    public void storesEntries() throws Exception {

        givenNoStoreFile();

        fileStore.store(new StoreEntry(new Login("www.site.com", "Bill"), Password.from("another secret")));

        storeFileContains(writtenData());

        assertThat(
                fileStore
                        .stream()
                        .collect(Collectors.toList()),
                hasItem(new StoreEntry(new Login("www.site.com", "Bill"), Password.from("another secret"))));
    }

    @Test(expected = StoreReadFailed.class)
    public void translatesIoErrorToStoreReadFailure() throws IOException {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(true);
        when(fileIO.readAllFrom(TEST_FILE)).thenThrow(new IOException());

        fileStore.stream();
    }

    @Test(expected = EntryNotStored.class)
    public void translatesIoErrorToStoreWriteFailure() throws IOException {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(false);
        doThrow(new IOException()).when(fileIO).writeAllTo(eq(TEST_FILE), any(byte[].class));

        fileStore.store(new StoreEntry(new Login("www.site.com", "Bill"), Password.from("another secret")));
    }

    @Test
    public void copiesEntriesToOtherStore() throws IOException {

        final SecureStore otherStore = mock(SecureStore.class);
        final StoreEntry entry = new StoreEntry(new Login("www.site.com", "Bill"), Password.from("another secret"));
        storeContains(entry);

        fileStore.copyTo(otherStore);

        verify(otherStore).store(entry);
    }

    private void givenNoStoreFile() {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(false);
    }

    private void storeContains(final StoreEntry entry) throws IOException {

        fileStore.store(entry);
        storeFileContains(writtenData());
    }

    private void storeFileContains(final byte[] value) throws IOException {

        when(fileIO.fileExists(TEST_FILE)).thenReturn(true);
        when(fileIO.readAllFrom(TEST_FILE)).thenReturn(value);
    }

    private byte[] writtenData() throws IOException {

        final ArgumentCaptor<byte[]> dataCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(fileIO).writeAllTo(eq(TEST_FILE), dataCaptor.capture());
        return dataCaptor.getValue();
    }
}
