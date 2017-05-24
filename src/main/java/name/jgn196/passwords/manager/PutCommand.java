package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.core.StoreFile;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;

import static java.lang.String.join;
import static java.lang.System.lineSeparator;

class PutCommand extends Command {

    static final String NAME = "put";
    static final String USAGE = join(lineSeparator(),
            "Usage: KeyRing put <system> <user>",
            SYSTEM_ARGUMENT_HELP,
            USER_ARGUMENT_HELP);

    private final String[] args;

    private Login login;

    PutCommand(final Console console, final StoreFile storeFile, final String... args) {

        super(console, storeFile);
        this.args = args;
    }

    @Override
    public void run() {

        if (!parseArguments()) return;

        putPassword();
    }

    private void putPassword() {
        try (final Password password = readPassword();
             final Safe safe = new Safe(storeFile(), readStorePassword())) {

            safe.store(login, password);
            printLineToConsole("Password stored");

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

    private Password readPassword() {

        return readPasswordFromConsole("Password for " + displayText(login) + ":");
    }
}
