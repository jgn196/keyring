package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;

import java.util.Optional;

class GetCommand extends Command {

    static final String NAME = "get";
    static final String USAGE = "get usage: system user";

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
             final Safe safe = new Safe(openWithPassword(storePassword))) {

            printPasswordFrom(safe);

        } catch(Exception e) {
            // Failed to close store - ignored
        }
    }

    private void printPasswordFrom(final Safe safe) {
        try {

            final Optional<Password> password = safe.passwordFor(login);

            if (password.isPresent())
                consolePrint(new String(password.get().characters()));
            else
                consolePrint("Password for " + displayText(login) + " not found.");
        } catch(DecryptionFailed e) {

            consolePrint(INCORRECT_STORE_PASSWORD);
        }
    }

    private boolean parseArguments() {

        if (args.length < 3) {
            consolePrint(USAGE);
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }
}

