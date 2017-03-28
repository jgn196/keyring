package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;

abstract class Command {

    static final String LIST_COMMAND = "list";
    static final String GET_COMMAND = "get";
    static final String PUT_COMMAND = "put";

    static Command parseCommandFrom(final String[] args) {

        final String commandWord = args.length == 0 ? "" : args[0];

        switch (commandWord) {
            case LIST_COMMAND:
                return new NoDataFileCommand();
            case GET_COMMAND:
                return new GetCommand();
            case PUT_COMMAND:
                return new PutCommand();
            default:
                return new HelpCommand();
        }
    }

    abstract void run(Console console);
}

class GetCommand extends Command {

    @Override
    void run(final Console console) {

        if (!Paths.get("passwords.dat").toFile().exists()) {
            console.print(NoDataFileCommand.NO_DATA_FILE_MESSAGE);
            return;
        }

        try (final Password storePassword = readStorePassword(console)) {

            console.print(
                    new String(
                            new Safe(new FileStore(Paths.get("passwords.dat").toFile(), storePassword))
                                    .passwordFor(new Login("www.site.com", "Bill")).get().characters()));
        }
    }

    private Password readStorePassword(final Console console) {

        console.print("Password for store:");
        return new Password(console.readPassword());
    }
}

class NoDataFileCommand extends Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";

    @Override
    public void run(final Console console) {

        console.print(NO_DATA_FILE_MESSAGE);
    }
}