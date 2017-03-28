package name.jgn196.passwords.manager;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;

public class AManager {

    @Test
    public void printsUsage() throws IOException {

        final TestConsole console = new TestConsole();
        Manager.useConsole(console);
        Manager.main();

        assertEquals(HelpCommand.USAGE, console.capturedOutput());
    }

    @Test
    public void listsNoLoginsWhenThereIsNoDataFile() throws IOException {

        final TestConsole console = new TestConsole();
        givenNoDataFile();
        Manager.useConsole(console);
        Manager.main(Command.LIST_COMMAND);

        assertEquals(NoDataFileCommand.NO_DATA_FILE_MESSAGE, console.capturedOutput());
    }

    @Test
    public void getPasswordWhenThereIsNoDataFile() throws IOException {

        final TestConsole console = new TestConsole();
        givenNoDataFile();
        Manager.useConsole(console);

        Manager.main(Command.GET_COMMAND, "www.site.com", "Bill");

        assertEquals(NoDataFileCommand.NO_DATA_FILE_MESSAGE, console.capturedOutput());
    }

    @Test
    public void storeFirstPassword() throws IOException {

        final TestConsole console = new TestConsole();
        givenNoDataFile();
        Manager.useConsole(console);
        console.prepareInput("bill_password", "file_password");

        Manager.main(Command.PUT_COMMAND, "www.site.com", "Bill");

        assertThat(
                console.capturedOutput(),
                stringContainsInOrder(asList("Password for Bill @ www.site.com:", "Password for store:")));
        assertTrue(Files.exists(Paths.get("passwords.dat")));
    }

    private void givenNoDataFile() throws IOException {

        final String dataFileName = "passwords.dat";

        if (Files.exists(Paths.get(dataFileName)))
            Files.delete(Paths.get(dataFileName));
    }
}