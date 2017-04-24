package name.jgn196.passwords.manager;

class HelpCommand extends Command {

    static final String USAGE = "Commands:\n" +
            "\tlist\tLists all the stored logins.\n" +
            "\tget\tGet a password for a login.\n" +
            "\tput\tPut a password for a login into the store.\n" +
            "\tremove\tRemove a password for a login.";

    HelpCommand(final Console console, final StoreFile storeFile) {

        super(console, storeFile);
    }

    @Override
    public void run() {

        printToConsole(USAGE);
    }
}
