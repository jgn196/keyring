package name.jgn196.passwords.manager;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class AManager {

    private ByteArrayOutputStream out;

    @Before
    public void captureOutput() {

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    public void printsUsage() {

        Manager.main(new String[0]);

        usagePrinted();
    }

    private void usagePrinted() {

        assertEquals("Commands:", capturedOutput());
    }

    private String capturedOutput() {

        return new String(out.toByteArray());
    }
}
