package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import org.junit.Test;

import java.io.IOException;

import static name.jgn196.passwords.manager.Command.INCORRECT_STORE_PASSWORD;
import static name.jgn196.passwords.manager.Command.NO_DATA_FILE_MESSAGE;
import static name.jgn196.passwords.manager.Manager.STORE_FILE;
import static name.jgn196.passwords.manager.Preconditions.givenNoDataFile;
import static name.jgn196.passwords.manager.Preconditions.givenStoreWithPassword;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AListCommand {

    private TestConsole console = new TestConsole();

    @Test
    public void failsWithMissingStore() throws IOException {

        givenNoDataFile();

        new ListCommand(console, STORE_FILE).run();

        assertEquals(NO_DATA_FILE_MESSAGE, console.capturedOutput());
    }

    @Test
    public void failsWithWrongStorePassword() throws IOException {

        console.prepareInput("wrong_store_password");
        givenStoreWithPassword("store_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        new ListCommand(console, STORE_FILE).run();

        assertThat(console.capturedOutput(), endsWith(INCORRECT_STORE_PASSWORD));
    }

    @Test
    public void listsLogins() throws IOException {

        console.prepareInput("store_password");
        givenStoreWithPassword("store_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        new ListCommand(console, STORE_FILE).run();

        assertThat(console.capturedOutput(), containsString("Bill @ www.site.com"));
    }
}
