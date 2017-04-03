package name.jgn196.passwords.manager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class APutCommand {

    @Test
    public void printsUsage() {

        final TestConsole console = new TestConsole();

        new PutCommand().run(console);

        assertEquals("put usage: system user", console.capturedOutput());
    }
}
