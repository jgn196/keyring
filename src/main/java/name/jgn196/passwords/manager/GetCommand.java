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

    private Console console;
    private Login login;

    GetCommand(final String... args) {

        this.args = args;
    }

    @Override
    void run(final Console console) {

        this.console = console;

        if (!parseArguments()) return;
        if (!checkStoreExists(console)) return;

        printPassword();
    }

    private void printPassword() {
        try (final Password storePassword = readStorePassword(console);
             final Safe safe = new Safe(StoreFile.openWithPassword(storePassword))) {

            printPasswordFrom(safe);

        } catch(Exception e) {
            // Failed to close store - ignored
        }
    }

    private void printPasswordFrom(final Safe safe) {
        try {

            final Optional<Password> password = safe.passwordFor(login);

            if (password.isPresent())
                console.print(new String(password.get().characters()));
            else
                console.print("Password for " + displayText(login) + " not found.");
        } catch(DecryptionFailed e) {

            console.print(INCORRECT_STORE_PASSWORD);
        }
    }

    private boolean parseArguments() {

        if (args.length < 3) {
            console.print(USAGE);
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }
}

