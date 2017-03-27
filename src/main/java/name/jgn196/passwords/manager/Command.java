package name.jgn196.passwords.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

abstract class Command implements Runnable {

    static final String LIST_COMMAND = "list";
    static final String GET_COMMAND = "get";
    static final String PUT_COMMAND = "put";

    static Command parseCommandFrom(final String[] args) {

        final String commandWord = args.length == 0 ? "" : args[0];

        switch(commandWord) {
            case LIST_COMMAND:
            case GET_COMMAND:
                return new NoDataFileCommand();
            case PUT_COMMAND:
                return new PutCommand();
            default:
                return new HelpCommand();
        }
    }
}

class HelpCommand extends Command {

    static final String USAGE = "Commands:\n" +
            "\tlist\tLists all the stored logins.\n" +
            "\tget\tGet a password for a login.";

    @Override
    public void run() {

        System.out.print(USAGE);
    }
}

class NoDataFileCommand extends Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";

    @Override
    public void run() {

        System.out.print(NO_DATA_FILE_MESSAGE);
    }
}

class PutCommand extends Command {

    @Override
    public void run() {
        try {

            System.out.print("Password for Bill @ www.site.com:");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.print("Password for store:");
            if (!Paths.get("passwords.dat").toFile().createNewFile())
                throw new IOException("Failed to create password store.");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}