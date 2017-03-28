package name.jgn196.passwords.manager;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Arrays.asList;
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
    public void getPasswordWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        Manager.main(Command.GET_COMMAND, "www.site.com", "Bill");

        assertEquals(NoDataFileCommand.NO_DATA_FILE_MESSAGE, capturedOutput());
    }

    @Test
    public void storeFirstPassword() throws IOException {

        givenNoDataFile();
        givenInput("bill_password", "file_password");

        Manager.main(Command.PUT_COMMAND, "www.site.com", "Bill");

        assertThat(
                capturedOutput(),
                stringContainsInOrder(asList("Password for Bill @ www.site.com:", "Password for store:")));
        assertTrue(Files.exists(Paths.get("passwords.dat")));
    }

    private void givenNoDataFile() throws IOException {

        final String dataFileName = "passwords.dat";

        if (Files.exists(Paths.get(dataFileName)))
            Files.delete(Paths.get(dataFileName));
    }

    private void givenInput(final String... strings) {
        console.prepareInput(strings);
    }

    private String capturedOutput() {

        return console.capturedOutput();
    }
}