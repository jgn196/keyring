package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.core.StoreFile;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;

import static java.lang.String.join;
import static java.lang.System.lineSeparator;

class RemoveCommand extends Command {

    static final String NAME = "remove";
    static final String USAGE = join(lineSeparator(),
            "Usage: KeyRing remove <system> <user>",
            SYSTEM_ARGUMENT_HELP,
            USER_ARGUMENT_HELP);

    private final String[] args;

    private Login login;

    RemoveCommand(final Console console, final StoreFile storeFile, final String... args) {

        super(console, storeFile);
        this.args = args;
    }

    @Override
    void run() {

        if (!parseArguments()) return;
        if (!checkStoreExists()) return;

        removePassword();
    }

    private boolean parseArguments() {

        if (args.length < 3) {
            printLineToConsole(USAGE);
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }

    private void removePassword() {
        try (final Password storePassword = readStorePassword();
             final Safe safe = new Safe(storeFile(), storePassword)) {

            if (safe.remove(login)) printLineToConsole("Password removed");
            else printLineToConsole("Password for " + displayText(login) + " not found.");

        } catch (DecryptionFailed e) {
            printLineToConsole(INCORRECT_STORE_PASSWORD);
        }
    }
}
