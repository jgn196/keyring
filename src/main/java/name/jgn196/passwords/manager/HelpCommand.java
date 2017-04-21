package name.jgn196.passwords.manager;

class HelpCommand extends Command {

    static final String USAGE = "Commands:\n" +
            "\tlist\tLists all the stored logins.\n" +
            "\tget\tGet a password for a login.\n" +
            "\tput\tPut a password for a login into the store.\n" +
            "\tremove\tRemove a password for a login.";

    @Override
    public void run(final Console console) {

        console.print(USAGE);
    }
}
