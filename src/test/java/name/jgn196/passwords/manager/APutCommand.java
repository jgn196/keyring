package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static name.jgn196.passwords.manager.Command.INCORRECT_STORE_PASSWORD;
import static name.jgn196.passwords.manager.PostConditions.storedLogins;
import static name.jgn196.passwords.manager.PostConditions.storedPassword;
import static name.jgn196.passwords.manager.Preconditions.givenNoDataFile;
import static name.jgn196.passwords.manager.Preconditions.givenStoreWithPassword;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class APutCommand {

    private final TestConsole console = new TestConsole();

    @Test
    public void printsUsage() {

        new PutCommand(console, "put").run();

        assertEquals(PutCommand.USAGE, console.capturedOutput());
    }

    @Test
    public void failsWithWrongStorePassword() throws IOException {

        Preconditions.givenStoreWithPassword("file_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("bill_password", "wrong_store_password");
        new PutCommand(console, "put", "www.site.com", "Bill").run();

        assertThat(console.capturedOutput(), containsString(INCORRECT_STORE_PASSWORD));
    }

    @Test
    public void storesNewLogin() throws IOException {

        givenNoDataFile();

        console.prepareInput("bill_password", "store_password");
        new PutCommand(console, "put", "www.site.com", "Bill").run();

        assertThat(storedLogins("store_password"), hasItem(new Login("www.site.com", "Bill")));
    }

    @Test
    public void storesPassword() throws IOException {

        givenNoDataFile();

        console.prepareInput("bill_password", "store_password");
        new PutCommand(console, "put", "www.site.com", "Bill").run();

        assertEquals(
                Optional.of(Password.from("bill_password")),
                storedPassword(new Login("www.site.com", "Bill"), "store_password"));
    }

    @Test
    public void updatesPassword() throws IOException {

        givenStoreWithPassword("store_password")
                .containing(new Login("www.site.com", "Bill"), "bill_password");

        console.prepareInput("bill_password2", "store_password");
        new PutCommand(console, "put", "www.site.com", "Bill").run();

        assertEquals(
                Optional.of(Password.from("bill_password2")),
                storedPassword(new Login("www.site.com", "Bill"), "store_password"));
    }
}
