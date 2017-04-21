package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.util.Arrays.asList;
import static name.jgn196.passwords.manager.Command.INCORRECT_STORE_PASSWORD;
import static org.hamcrest.Matchers.*;
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

        Preconditions.givenNoDataFile();

        Manager.main(ListCommand.NAME);

        assertEquals(Command.NO_DATA_FILE_MESSAGE, capturedOutput());
    }

    @Test
    public void failsToGetPasswordWhenThereIsNoDataFile() throws IOException {

        Preconditions.givenNoDataFile();

        Manager.main(GetCommand.NAME, "www.site.com", "Bill");

        assertEquals(Command.NO_DATA_FILE_MESSAGE, capturedOutput());
    }

    @Test
    public void createsPasswordStoreWhenPuttingFirstPassword() throws IOException {

        Preconditions.givenNoDataFile();
        givenInput("bill_password", "file_password");

        Manager.main(PutCommand.NAME, "www.site.com", "Bill");

        assertTrue(StoreFile.exists());
    }

    @Test
    public void promptsForPasswordsWhenStoring() throws IOException {

        givenInput("bill_password", "file_password");

        Manager.main(PutCommand.NAME, "www.site.com", "Bill");

        assertThat(
                capturedOutput(),
                stringContainsInOrder(asList("Password for Bill @ www.site.com:", "Password for store:")));
    }

    @Test
    public void encryptsPasswordInStoreFile() throws IOException {

        final String password = "bill_password";
        givenInput(password, "file_password");

        Manager.main(PutCommand.NAME, "www.site.com", "Bill");

        assertFalse(
                "Found password '" + password + "' in plain text in store file.",
                fileContains(password));
    }

    @Test
    public void getsStoredPassword() throws IOException {

        Preconditions.givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        givenInput("file_password");
        Manager.main(GetCommand.NAME, "www.site.com", "Bill");

        assertThat(capturedOutput(), endsWith("bill_password"));
    }

    @Test
    public void failsToGetPasswordWithWrongStorePassword() throws IOException {

        Preconditions.givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        givenInput("wrong_password");
        Manager.main(GetCommand.NAME, "www.site.com", "Bill");

        assertThat(capturedOutput(), endsWith(INCORRECT_STORE_PASSWORD));
    }

    @Test
    public void failsToGetMissingPassword() throws IOException {

        Preconditions.givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        givenInput("file_password");
        Manager.main(GetCommand.NAME, "www.site.com", "Ted");

        assertThat(capturedOutput(), endsWith("Password for Ted @ www.site.com not found."));
    }

    @Test
    public void listsLogins() throws IOException {

        Preconditions.givenStoreWithPassword("file_password").containing(new Login("www.site.com", "Bill"), "bill_password");

        givenInput("file_password");
        Manager.main(ListCommand.NAME);

        assertThat(capturedOutput(), endsWith("Passwords stored for:\n\tBill @ www.site.com\n"));
    }

    @Test
    @Ignore("Login deletion not implemented yet.")
    public void deletesLogins() throws IOException {

        Preconditions.givenStoreWithPassword("file_password").containing(new Login("www.site.com", "Bill"), "bill_password");

        givenInput("file_password");
        Manager.main(RemoveCommand.NAME, "www.site.com", "Bill");

        assertThat(PostConditions.storedLogins("file_password"), not(hasItem(new Login("www.site.com", "Bill"))));
    }

    private void givenInput(final String... strings) {

        console.prepareInput(strings);
    }

    private String capturedOutput() {

        return console.capturedOutput();
    }

    private boolean fileContains(final String pattern) throws IOException {

        final byte[] fileData = Files.readAllBytes(StoreFile.toPath());

        return contains(fileData, pattern.getBytes(StandardCharsets.UTF_8)) ||
                contains(fileData, pattern.getBytes(StandardCharsets.UTF_16LE)) ||
                contains(fileData, pattern.getBytes(StandardCharsets.UTF_16BE));
    }

    private boolean contains(byte[] data, byte[] pattern) {

        return KMPMatch.indexOf(data, pattern) != -1;
    }
}
