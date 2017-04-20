package name.jgn196.passwords.manager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AHelpCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new HelpCommand().run(console);

        assertEquals(HelpCommand.USAGE, console.capturedOutput());
    }
}
