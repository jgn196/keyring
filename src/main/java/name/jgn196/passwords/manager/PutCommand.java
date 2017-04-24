package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;

class PutCommand extends Command {

    static final String NAME = "put";
    static final String USAGE = "put usage: system user";

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
             final Safe safe = new Safe(openWithPassword(readStorePassword()))) {

            putPassword(password, safe);

        } catch (Exception e) {
            // Failed to close store - ignored
        }
    }

    private boolean parseArguments() {

        if (args.length < 3) {

            printToConsole(USAGE);
            return false;
        }

        login = new Login(args[1], args[2]);
        return true;
    }

    private Password readPassword() {

        return readPasswordFromConsole("Password for " + displayText(login) + ":");
    }

    private void putPassword(final Password password, final Safe safe) {
        try {

            safe.store(login, password);

        } catch (DecryptionFailed e) {
            printToConsole(INCORRECT_STORE_PASSWORD);
        }
    }
}
