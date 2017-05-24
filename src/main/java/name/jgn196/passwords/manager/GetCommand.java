package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.core.StoreFile;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;

import java.util.Optional;

import static java.lang.String.join;
import static java.lang.System.lineSeparator;

class GetCommand extends Command {

    static final String NAME = "get";
    static final String USAGE = join(lineSeparator(),
            "Usage: KeyRing get <system> <user>",
            SYSTEM_ARGUMENT_HELP,
            USER_ARGUMENT_HELP);

    private final String[] args;

    private Login login;

    GetCommand(final Console console, final StoreFile storeFile, final String... args) {

        super(console, storeFile);
        this.args = args;
    }

    @Override
    void run() {

        if (!parseArguments()) return;
        if (!checkStoreExists()) return;

        printPassword();
    }

    private void printPassword() {
        try (final Password storePassword = readStorePassword();
             final Safe safe = new Safe(storeFile(), storePassword)) {

            printPasswordFrom(safe);

        } catch (Exception e) {
            // Failed to close store - ignored
        }
    }

    private void printPasswordFrom(final Safe safe) {
        try {

            final Optional<Password> password = safe.passwordFor(login);

            if (password.isPresent())
                printLineToConsole(new String(password.get().characters()));
            else
                printLineToConsole("Password for " + displayText(login) + " not found.");
        } catch (DecryptionFailed e) {

            printLineToConsole(INCORRECT_STORE_PASSWORD);
        }
    }

    private boolean parseArguments() {

        if (args.length < 3) {
            printLineToConsole(USAGE);
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }
}

