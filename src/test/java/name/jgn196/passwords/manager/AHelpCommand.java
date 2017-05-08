package name.jgn196.passwords.manager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AHelpCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new HelpCommand(console).run();

        assertEquals(HelpCommand.USAGE, console.capturedOutput());
    }
}
