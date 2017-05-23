package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.core.StoreFile;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;

class RemoveCommand extends Command {

    static final String NAME = "remove";
    static final String USAGE = "Usage: KeyRing remove <system> <user>\n" +
            SYSTEM_ARGUMENT_HELP + "\n" +
            USER_ARGUMENT_HELP;

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
            printToConsole(USAGE);
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }

    private void removePassword() {
        try (final Password storePassword = readStorePassword();
             final Safe safe = new Safe(storeFile(), storePassword)) {

            if (safe.remove(login)) printToConsole("Password removed");
            else printToConsole("Password for " + displayText(login) + " not found.");

        } catch (DecryptionFailed e) {
            printToConsole(INCORRECT_STORE_PASSWORD);
        }
    }
}
