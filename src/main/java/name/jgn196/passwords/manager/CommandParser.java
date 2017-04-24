package name.jgn196.passwords.manager;

class CommandParser {

    private final Console console;
    private final StoreFile storeFile;

    CommandParser(final Console console, final StoreFile storeFile) {

        this.console = console;
        this.storeFile = storeFile;
    }

    Command parse(final String... args) {

        final String commandWord = args.length == 0 ? "" : args[0];

        switch (commandWord) {
            case ListCommand.NAME:
                return new ListCommand(console, storeFile);
            case GetCommand.NAME:
                return new GetCommand(console, storeFile, args);
            case PutCommand.NAME:
                return new PutCommand(console, storeFile, args);
            case RemoveCommand.NAME:
                return new RemoveCommand(console, storeFile, args);
            default:
                return new HelpCommand(console, storeFile);
        }
    }
}
