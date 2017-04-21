package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;

class ListCommand extends Command {

    static final String NAME = "list";

    private Console console;

    @Override
    public void run(final Console console) {

        this.console = console;

        if (!checkStoreExists(console)) return;

        printLogins();
    }

    private void printLogins() {
        try (final Password storePassword = readStorePassword(console);
             final Safe safe = new Safe(StoreFile.openWithPassword(storePassword))) {

            printLoginsFrom(safe);

        } catch (Exception e) {
            // Failed to close store - ignored
        }
    }

    private void printLoginsFrom(final Safe safe) {
        try {

            console.print("Passwords stored for:\n");
            safe.logins()
                    .forEach(login -> console.print("\t" + displayText(login) + "\n"));
        } catch (DecryptionFailed e) {

            console.print(INCORRECT_STORE_PASSWORD);
        }
    }
}
