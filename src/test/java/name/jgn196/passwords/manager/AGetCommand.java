package name.jgn196.passwords.manager;

import org.junit.Test;

import java.io.IOException;

import static name.jgn196.passwords.manager.AManager.givenNoDataFile;
import static name.jgn196.passwords.manager.Command.NO_DATA_FILE_MESSAGE;
import static org.junit.Assert.assertEquals;

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
}
