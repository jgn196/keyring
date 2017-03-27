package name.jgn196.passwords.manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;

public class AManager {

    private ByteArrayOutputStream out;
    private PrintStream originalOut;
    private PrintStream inputPipe;
    private InputStream originalIn;

    @Before
    public void captureOutput() {

        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Before
    public void openInputPipe() throws IOException {

        final PipedOutputStream src = new PipedOutputStream();

        originalIn = System.in;
        inputPipe = new PrintStream(src);

        System.setIn(new PipedInputStream(src));
    }

    @Test
    public void printsUsage() throws IOException {

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

    @Test
    public void storeFirstPassword() throws IOException {

        givenNoDataFile();
        sendInput("bill_password");
        sendInput("file_password");

        Manager.main("put", "www.site.com", "Bill");

        assertThat(
                capturedOutput(),
                stringContainsInOrder(asList("Password for Bill @ www.site.com:", "Password for store:")));
        assertTrue(Files.exists(Paths.get("passwords.dat")));
    }

    @After
    public void restoreOutput() {

        System.setOut(originalOut);
    }

    @After
    public void restoreInput() {

        System.setIn(originalIn);
    }

    private void givenNoDataFile() throws IOException {

        final String dataFileName = "passwords.dat";

        if (Files.exists(Paths.get(dataFileName)))
            Files.delete(Paths.get(dataFileName));
    }

    private String capturedOutput() {

        return new String(out.toByteArray());
    }

    private void sendInput(final String input) {

        inputPipe.println(input);
    }
}
