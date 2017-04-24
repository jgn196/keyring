package name.jgn196.passwords.manager;

import java.io.IOException;

public class Manager {

    private static final String STORE_FILE_NAME = "passwords.dat";
    private static final Console DEFAULT_CONSOLE = new Console();

    static final StoreFile STORE_FILE = new StoreFile(STORE_FILE_NAME);

    private Console console;

    public static void main(final String... args) throws IOException {

        new Manager(DEFAULT_CONSOLE)
                .run(args);
    }

    Manager(final Console console) {

        this.console = console;
    }

    void run(final String... args) {

        new CommandParser(console, STORE_FILE)
                .parse(args)
                .run();
    }
}
