package name.jgn196.passwords.manager;

class CommandParser {

    private final Console console;

    CommandParser(final Console console) {

        this.console = console;
    }

    Command parse(final String... args) {

        final String commandWord = args.length == 0 ? "" : args[0];

        switch (commandWord) {
            case ListCommand.NAME:
                return new ListCommand(console);
            case GetCommand.NAME:
                return new GetCommand(console, args);
            case PutCommand.NAME:
                return new PutCommand(console, args);
            case RemoveCommand.NAME:
                return new RemoveCommand(console, args);
            default:
                return new HelpCommand(console);
        }
    }
}
