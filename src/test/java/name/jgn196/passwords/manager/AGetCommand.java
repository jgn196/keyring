package name.jgn196.passwords.manager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AGetCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new GetCommand().run(console);

        assertEquals("get usage: system user", console.capturedOutput());
    }
}
