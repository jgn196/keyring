package name.jgn196.passwords.manager;

import java.io.IOException;

public class Manager {

    static final String STORE_FILE_NAME = "passwords.dat";

    private static Console console = new Console();

    static void useConsole(final Console console) {

        Manager.console = console;
    }

    public static void main(final String... args) throws IOException {

        Command.parseCommandFrom(args).run(console);
    }
}