package name.jgn196.passwords.manager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Arrays.asList;
import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;

public class AManager {

    private final TestConsole console = new TestConsole();

    @Before
    public void setUpTestConsole() {

        Manager.useConsole(console);
    }

    @Test
    public void printsUsage() throws IOException {

        Manager.main();

        assertEquals(HelpCommand.USAGE, capturedOutput());
    }

    @Test
    public void listsNoLoginsWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        Manager.main(Command.LIST_COMMAND);

        assertEquals(NoDataFileCommand.NO_DATA_FILE_MESSAGE, capturedOutput());
    }

    @Test
    public void getsPasswordWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        Manager.main(Command.GET_COMMAND, "www.site.com", "Bill");

        assertEquals(NoDataFileCommand.NO_DATA_FILE_MESSAGE, capturedOutput());
    }

    @Test
    public void createsPasswordStoreWhenPuttingFirstPassword() throws IOException {

        givenNoDataFile();
        givenInput("bill_password", "file_password");

        Manager.main(Command.PUT_COMMAND, "www.site.com", "Bill");

        assertTrue(Files.exists(Paths.get(STORE_FILE_NAME)));
    }

    @Test
    public void storesPassword() throws IOException {

        givenInput("bill_password", "file_password");

        Manager.main(Command.PUT_COMMAND, "www.site.com", "Bill");

        assertThat(
                capturedOutput(),
                stringContainsInOrder(asList("Password for Bill @ www.site.com:", "Password for store:")));
    }

    @Test
    @Ignore // TODO - Wire in encryption
    public void encryptsPasswordInStoreFile() throws IOException {

        final String password = "bill_password";
        givenInput(password, "file_password");

        Manager.main(Command.PUT_COMMAND, "www.site.com", "Bill");

        assertFalse(
                "Found password '" + password + "' in plain text in store file.",
                fileContains(Manager.STORE_FILE_NAME, password));
    }

    @Test
    public void getsPassword() throws IOException {

        givenNoDataFile();
        givenInput("bill_password", "file_password");
        Manager.main(Command.PUT_COMMAND, "www.site.com", "Bill");

        Manager.main(Command.GET_COMMAND, "www.site.com", "Bill");

        assertThat(capturedOutput(), endsWith("bill_password"));
    }

    private void givenNoDataFile() throws IOException {

        if (Files.exists(Paths.get(STORE_FILE_NAME)))
            Files.delete(Paths.get(STORE_FILE_NAME));
    }

    private void givenInput(final String... strings) {
        console.prepareInput(strings);
    }

    private String capturedOutput() {

        return console.capturedOutput();
    }

    private boolean fileContains(final String fileName, final String pattern) throws IOException {

        final byte[] fileData = Files.readAllBytes(Paths.get(fileName));

        return contains(fileData, pattern.getBytes(StandardCharsets.UTF_8)) ||
                contains(fileData, pattern.getBytes(StandardCharsets.UTF_16LE)) ||
                contains(fileData, pattern.getBytes(StandardCharsets.UTF_16BE));
    }

    private boolean contains(byte[] data, byte[] pattern) {

        return KMPMatch.indexOf(data, pattern) != -1;
    }
}