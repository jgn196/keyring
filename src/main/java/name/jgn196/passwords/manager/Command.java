package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;

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
                return new GetCommand(args);
            case PUT_COMMAND:
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

class NoDataFileCommand extends Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";

    @Override
    public void run(final Console console) {

        console.print(NO_DATA_FILE_MESSAGE);
    }
}