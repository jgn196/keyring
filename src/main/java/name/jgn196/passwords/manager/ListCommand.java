package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.core.StoreFile;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;

class ListCommand extends Command {

    static final String NAME = "list";

    ListCommand(final Console console, final StoreFile storeFile) {

        super(console, storeFile);
    }

    @Override
    public void run() {

        if (!checkStoreExists()) return;

        printLogins();
    }

    private void printLogins() {
        try (final Password storePassword = readStorePassword();
             final Safe safe = new Safe(storeFile(), storePassword)) {

            printLoginsFrom(safe);

        } catch (Exception e) {
            // Failed to close store - ignored
        }
    }

    private void printLoginsFrom(final Safe safe) {
        try {

            printLineToConsole("Passwords stored for:");
            safe.logins()
                    .forEach(login -> printLineToConsole("\t" + displayText(login)));
        } catch (DecryptionFailed e) {

            printLineToConsole(INCORRECT_STORE_PASSWORD);
        }
    }
}
