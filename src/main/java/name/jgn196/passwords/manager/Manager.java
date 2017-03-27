package name.jgn196.passwords.manager;

import java.io.IOException;

public class Manager {

    public static void main(final String... args) throws IOException {

        Command.parseCommandFrom(args).run();
    }
}