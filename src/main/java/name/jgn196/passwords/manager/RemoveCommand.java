package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;

class RemoveCommand extends Command {

    static final String NAME = "remove";
    static final String USAGE = "remove usage: system user";

    private final String[] args;

    private Console console;
    private Login login;

    RemoveCommand(final String... args) {

        this.args = args;
    }

    @Override
    void run(final Console console) {

        this.console = console;

        if (!parseArguments()) return;
        if (!checkStoreExists(console)) return;

        removePassword();
    }

    private boolean parseArguments() {

        if (args.length < 3) {
            console.print(USAGE);
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }

    private void removePassword() {
        try (final Password storePassword = readStorePassword(console);
             final Safe safe = new Safe(StoreFile.openWithPassword(storePassword))) {

            removePasswordFrom(safe);

        } catch (Exception e) {
            // Failed to close store - ignored
        }
    }

    private void removePasswordFrom(Safe safe) {
        try {

            if (!safe.remove(login)) console.print("Password for " + displayText(login) + " not found.");

        } catch (DecryptionFailed e) {
            console.print(INCORRECT_STORE_PASSWORD);
        }
    }
}
