package name.jgn196.passwords.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Optional;

public class Manager {

    static final String USAGE = "Commands:\n" +
            "\tlist\tLists all the stored logins.\n" +
            "\tget\tGet a password for a login.";
    static final String LIST_COMMAND = "list";
    static final String GET_COMMAND = "get";
    static final String PUT_COMMAND = "put";
    static final String NO_DATA_FILE_MESSAGE = "No data file.";

    public static void main(final String... args) throws IOException {

        final Optional<String> command = commandFrom(args);

        if (!command.isPresent()) {
            System.out.print(USAGE);
            return;
        }

        switch(command.get()) {
            case LIST_COMMAND:
            case GET_COMMAND:
                System.out.print(NO_DATA_FILE_MESSAGE);
                break;
            case PUT_COMMAND:
                runPutCommand();
                break;
        }
    }

    private static Optional<String> commandFrom(final String[] args) {

        if (args.length > 0) return Optional.of(args[0]);
        return Optional.empty();
    }

    private static void runPutCommand() throws IOException {

        System.out.print("Password for Bill @ www.site.com:");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Password for store:");
        if (!Paths.get("passwords.dat").toFile().createNewFile())
            throw new IOException("Failed to create password store.");
    }
}
