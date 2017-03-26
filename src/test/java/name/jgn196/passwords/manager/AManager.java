package name.jgn196.passwords.manager;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        Manager.main();

        usagePrinted();
    }

    @Test
    public void listsNoLoginsWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        Manager.main("list");

        assertEquals("No data file.", capturedOutput());
    }

    private void usagePrinted() {

        assertEquals(
                "Commands:\n" +
                "\tlist\tLists all the stored logins.",
                capturedOutput());
    }

    private String capturedOutput() {

        return new String(out.toByteArray());
    }

    private void givenNoDataFile() throws IOException {

        if (Files.exists(Paths.get("passwords.dat")))
            Files.delete(Paths.get("passwords.dat"));
    }
}
