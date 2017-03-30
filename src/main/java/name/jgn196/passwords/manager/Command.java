package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;

abstract class Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";

    static Command parseCommandFrom(final String[] args) {

        final String commandWord = args.length == 0 ? "" : args[0];

        switch (commandWord) {
            case ListCommand.NAME:
                return new ListCommand();
            case GetCommand.NAME:
                return new GetCommand(args);
            case PutCommand.NAME:
                return new PutCommand(args);
            default:
                return new HelpCommand();
        }
    }

    static Password readStorePassword(final Console console) {

        console.print("Password for store:");
        return new Password(console.readPassword());
    }

    abstract void run(Console console);
}

