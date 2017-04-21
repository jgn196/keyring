package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static name.jgn196.passwords.manager.Command.INCORRECT_STORE_PASSWORD;
import static name.jgn196.passwords.manager.Command.NO_DATA_FILE_MESSAGE;
import static name.jgn196.passwords.manager.PostConditions.storedLogins;
import static name.jgn196.passwords.manager.Preconditions.givenNoDataFile;
import static name.jgn196.passwords.manager.Preconditions.givenStoreWithPassword;
import static name.jgn196.passwords.manager.RemoveCommand.USAGE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ARemoveCommand {

    private final TestConsole console = new TestConsole();

    @Test
    public void printsUsage() {

        new RemoveCommand("remove").run(console);

        assertEquals(USAGE, console.capturedOutput());
    }

    @Test
    public void failsToRemovePasswordWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        new RemoveCommand("remove", "www.site.com", "Bill").run(console);

        assertEquals(NO_DATA_FILE_MESSAGE, console.capturedOutput());
    }

    @Test
    public void failsToRemovePasswordWhenStorePasswordWrong() throws IOException {

        givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("wrong_password");
        new RemoveCommand("remove", "www.site.com", "Bill").run(console);

        assertThat(console.capturedOutput(), containsString(INCORRECT_STORE_PASSWORD));
    }

    @Test
    public void failsToRemoveMissingPassword() throws IOException {

        givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("file_password");
        new RemoveCommand("remove", "www.site.net", "Ted").run(console);

        assertThat(console.capturedOutput(), containsString("Password for Ted @ www.site.net not found."));
    }

    @Test
    public void removesPassword() throws IOException {

        givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("file_password");
        new RemoveCommand("remove", "www.site.com", "Bill").run(console);

        assertThat(storedLogins("file_password"), not(hasItem(new Login("www.site.com", "Bill"))));
    }
}
