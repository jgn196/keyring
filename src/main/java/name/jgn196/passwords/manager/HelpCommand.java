package name.jgn196.passwords.manager;

class HelpCommand extends Command {

    static final String USAGE = "Usage: KeyRing <command> [arguments...]\n" +
            "Commands:\n" +
            "\tlist\tLists all the stored logins.\n" +
            "\tget\tGet a password for a login.\n" +
            "\tput\tPut a password for a login into the store.\n" +
            "\tremove\tRemove a password for a login.\n" +
            "\tchange_password\tChange the store password.";

    HelpCommand(final Console console) {

        super(console, null);
    }

    @Override
    public void run() {

        printToConsole(USAGE);
    }
}
