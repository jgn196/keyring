package name.jgn196.passwords.manager;

class HelpCommand extends Command {

    static final String USAGE = String.join(System.lineSeparator(),
            "Usage: KeyRing <command> [arguments...]",
            "Commands:",
            "\tlist\tLists all the stored logins.",
            "\tget\tGet a password for a login.",
            "\tput\tPut a password for a login into the store.",
            "\tremove\tRemove a password for a login.",
            "\tchange_password\tChange the store password.");

    HelpCommand(final Console console) {

        super(console, null);
    }

    @Override
    public void run() {

        printLineToConsole("KeyRing " + Application.version());
        printLineToConsole(USAGE);
    }

}
