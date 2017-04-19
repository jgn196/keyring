package name.jgn196.passwords.manager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ARemoveCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new RemoveCommand("remove").run(console);

        assertEquals("remove usage: system user", console.capturedOutput());
    }
}
