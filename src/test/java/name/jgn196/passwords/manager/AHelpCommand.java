package name.jgn196.passwords.manager;

import org.junit.Test;

import static name.jgn196.passwords.manager.Manager.STORE_FILE;
import static org.junit.Assert.assertEquals;

public class AHelpCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new HelpCommand(console, STORE_FILE).run();

        assertEquals(HelpCommand.USAGE, console.capturedOutput());
    }
}
