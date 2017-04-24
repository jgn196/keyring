package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;

class ListCommand extends Command {

    static final String NAME = "list";

    ListCommand(final Console console) {
        super(console);
    }

    @Override
    public void run() {

        if (!checkStoreExists()) return;

        printLogins();
    }

    private void printLogins() {
        try (final Password storePassword = readStorePassword();
             final Safe safe = new Safe(StoreFile.openWithPassword(storePassword))) {

            printLoginsFrom(safe);

        } catch (Exception e) {
            // Failed to close store - ignored
        }
    }

    private void printLoginsFrom(final Safe safe) {
        try {

            consolePrint("Passwords stored for:\n");
            safe.logins()
                    .forEach(login -> consolePrint("\t" + displayText(login) + "\n"));
        } catch (DecryptionFailed e) {

            consolePrint(INCORRECT_STORE_PASSWORD);
        }
    }
}
