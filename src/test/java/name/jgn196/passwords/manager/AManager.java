package name.jgn196.passwords.manager;

import org.junit.After;
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
    private PrintStream originalOut;

    @Before
    public void captureOutput() {

        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    public void printsUsage() {

        Manager.main();

        assertEquals(Manager.USAGE, capturedOutput());
    }

    @Test
    public void listsNoLoginsWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        Manager.main(Manager.LIST_COMMAND);

        assertEquals(Manager.NO_DATA_FILE_MESSAGE, capturedOutput());
    }

    @Test
    public void getPasswordWhenThereIsNoDataFile() throws IOException {

        givenNoDataFile();

        Manager.main(Manager.GET_COMMAND, "www.site.com", "Bill");

        assertEquals(Manager.NO_DATA_FILE_MESSAGE, capturedOutput());
    }

    @After
    public void restoreOutput() {

        System.setOut(originalOut);
    }

    private String capturedOutput() {

        return new String(out.toByteArray());
    }

    private void givenNoDataFile() throws IOException {

        final String dataFileName = "passwords.dat";

        if (Files.exists(Paths.get(dataFileName)))
            Files.delete(Paths.get(dataFileName));
    }
}
