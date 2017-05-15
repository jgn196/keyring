package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import org.junit.Test;

import java.io.IOException;

import static name.jgn196.passwords.manager.Command.INCORRECT_STORE_PASSWORD;
import static name.jgn196.passwords.manager.Command.NO_DATA_FILE_MESSAGE;
import static name.jgn196.passwords.manager.Manager.STORE_FILE;
import static name.jgn196.passwords.manager.PostConditions.storedLogins;
import static name.jgn196.passwords.manager.Preconditions.givenNoDataFile;
import static name.jgn196.passwords.manager.Preconditions.givenStoreWithPassword;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AChangePasswordCommand {

    private final TestConsole console = new TestConsole();

    @Test
    public void failsIfNoFileStore() throws IOException {

        givenNoDataFile();

        new ChangePasswordCommand(console, STORE_FILE).run();

        assertEquals(NO_DATA_FILE_MESSAGE, console.capturedOutput());
    }

    @Test
    public void failsIfFirstPasswordWrong() throws IOException {

        givenStoreWithPassword("first password").containing(new Login("ignored", "ignored"), "ignored");
        console.prepareInput("wrong password", "new password");

        new ChangePasswordCommand(console, STORE_FILE).run();

        assertThat(console.capturedOutput(), containsString(INCORRECT_STORE_PASSWORD));
    }

    @Test
    public void changesFileStorePassword() throws IOException {

        givenStoreWithPassword("first password").containing(new Login("www.site.com", "Bill"), "ignored");
        console.prepareInput("first password", "new password");

        new ChangePasswordCommand(console, STORE_FILE).run();

        assertThat(storedLogins("new password"), hasItem(new Login("www.site.com", "Bill")));
    }
}
