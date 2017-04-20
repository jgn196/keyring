package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import org.junit.Test;

import java.io.IOException;

import static name.jgn196.passwords.manager.Command.INCORRECT_STORE_PASSWORD;
import static name.jgn196.passwords.manager.Command.NO_DATA_FILE_MESSAGE;
import static name.jgn196.passwords.manager.Preconditions.givenNoDataFile;
import static name.jgn196.passwords.manager.Preconditions.givenStoreWithPassword;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AGetCommand {

    private TestConsole console = new TestConsole();

    @Test
    public void printsUsage() {

        new GetCommand("get").run(console);

        assertEquals("get usage: system user", console.capturedOutput());
    }

    @Test
    public void failsToGetPasswordWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        new GetCommand("get", "www.site.com", "Bill").run(console);

        assertEquals(NO_DATA_FILE_MESSAGE, console.capturedOutput());
    }

    @Test
    public void failsToGetPasswordWhenStorePasswordWrong() throws IOException {

        givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("wrong_password");
        new GetCommand("get", "www.site.net", "Ted").run(console);

        assertThat(console.capturedOutput(), containsString(INCORRECT_STORE_PASSWORD));
    }

    @Test
    public void failsToGetMissingPassword() throws IOException {

        givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("file_password");
        new GetCommand("get", "www.site.net", "Ted").run(console);

        assertThat(console.capturedOutput(), containsString("Password for Ted @ www.site.net not found."));
    }

    @Test
    public void getsAPassword() throws IOException {

        givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("file_password");
        new GetCommand("get", "www.site.com", "Bill").run(console);

        assertThat(console.capturedOutput(), containsString("bill_password"));
    }
}
