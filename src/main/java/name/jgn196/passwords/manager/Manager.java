package name.jgn196.passwords.manager;

public class Manager {

    static final String LIST_COMMAND = "list";
    static final String GET_COMMAND = "get";
    static final String NO_DATA_FILE_MESSAGE = "No data file.";
    static final String USAGE = "Commands:\n" +
            "\tlist\tLists all the stored logins.\n" +
            "\tget\tGet a password for a login.";

    public static void main(final String... args) {

        if (args.length > 0 && (args[0].equals(LIST_COMMAND) || args[0].equals(GET_COMMAND)))
            System.out.print(NO_DATA_FILE_MESSAGE);
        else
            System.out.print(USAGE);
    }
}
