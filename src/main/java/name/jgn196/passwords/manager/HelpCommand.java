package name.jgn196.passwords.manager;

class HelpCommand extends Command {

    static final String USAGE = "Commands:\n" +
            "\tlist\tLists all the stored logins.\n" +
            "\tget\tGet a password for a login.";

    @Override
    public void run() {

        System.out.print(USAGE);
    }
}
