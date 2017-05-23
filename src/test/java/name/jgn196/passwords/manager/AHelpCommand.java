package name.jgn196.passwords.manager;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AHelpCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new HelpCommand(console).run();

        assertEquals(HelpCommand.USAGE, console.capturedOutput());
    }

    @Test
    @Ignore
    public void printsApplicationVersion() {

        final TestConsole console = new TestConsole();

        new HelpCommand(console).run();

        assertThat(console.capturedOutput(), containsString("1.0.0"));
    }
}
