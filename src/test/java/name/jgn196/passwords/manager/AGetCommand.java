package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;

import static name.jgn196.passwords.manager.Preconditions.givenNoDataFile;
import static name.jgn196.passwords.manager.Command.NO_DATA_FILE_MESSAGE;
import static name.jgn196.passwords.manager.Preconditions.givenStoreWithPassword;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AGetCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new GetCommand("get").run(console);

        assertEquals("get usage: system user", console.capturedOutput());
    }

    @Test
    public void failsToGetPasswordWhenThereIsNoDataFile() throws IOException {

        final TestConsole console = new TestConsole();
        givenNoDataFile();

        new GetCommand("get", "www.site.com", "Bill").run(console);

        assertEquals(NO_DATA_FILE_MESSAGE, console.capturedOutput());
    }

    @Test
    public void failsToGetMissingPassword() throws IOException {

        final TestConsole console = new TestConsole();
        console.prepareInput("file_password");
        givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        new GetCommand("get", "www.site.net", "Ted").run(console);

        assertThat(console.capturedOutput(), containsString("Password for Ted @ www.site.net not found."));
    }
}
