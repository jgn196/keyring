package name.jgn196.passwords.manager;

import java.io.IOException;

public class Manager {

    private static final String STORE_FILE_NAME = "passwords.dat";

    private static Console console = new Console();

    static final StoreFile STORE_FILE = new StoreFile(STORE_FILE_NAME);

    // This class is not for instantiation
    private Manager() {
    }

    static void useConsole(final Console console) {

        Manager.console = console;
    }

    public static void main(final String... args) throws IOException {

        new CommandParser(console, STORE_FILE)
                .parse(args)
                .run();
    }
}
