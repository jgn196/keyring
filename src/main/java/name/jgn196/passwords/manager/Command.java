package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;

abstract class Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";
    static final String INCORRECT_STORE_PASSWORD = "Incorrect store password.";

    static Command parseCommandFrom(final String[] args) {

        final String commandWord = args.length == 0 ? "" : args[0];

        switch (commandWord) {
            case ListCommand.NAME:
                return new ListCommand();
            case GetCommand.NAME:
                return new GetCommand(args);
            case PutCommand.NAME:
                return new PutCommand(args);
            case RemoveCommand.NAME:
                return new RemoveCommand(args);
            default:
                return new HelpCommand();
        }
    }

    static Password readStorePassword(final Console console) {

        console.print("Password for store:");
        return new Password(console.readPassword());
    }

    static String displayText(final Login login) {

        return login.userName() + " @ " + login.secureSystem();
    }

    abstract void run(Console console);

    boolean checkStoreExists(final Console console) {

        if (!StoreFile.exists()) console.print(NO_DATA_FILE_MESSAGE);
        return StoreFile.exists();
    }
}

