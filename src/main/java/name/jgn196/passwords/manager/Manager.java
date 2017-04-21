package name.jgn196.passwords.manager;

import java.io.IOException;

public class Manager {

    private static Console console = new Console();

    // This class is not for instantiation
    private Manager() { }

    static void useConsole(final Console console) {

        Manager.console = console;
    }

    public static void main(final String... args) throws IOException {

        Command.parseCommandFrom(args).run(console);
    }
}
