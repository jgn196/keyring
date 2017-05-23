package name.jgn196.passwords.manager;

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class AHelpCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new HelpCommand(console).run();

        assertThat(console.capturedOutput(), containsString(HelpCommand.USAGE));
    }

    @Test
    public void printsApplicationVersion() {

        final TestConsole console = new TestConsole();

        new HelpCommand(console).run();

        assertThat(console.capturedOutput(), containsString(Application.version()));
    }
}
