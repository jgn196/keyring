package name.jgn196.passwords.manager;

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

class NoDataFileCommand extends Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";

    @Override
    public void run() {

        System.out.print(NO_DATA_FILE_MESSAGE);
    }
}