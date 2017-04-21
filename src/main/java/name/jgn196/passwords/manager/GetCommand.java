package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;

import java.util.Optional;

class GetCommand extends Command {

    static final String NAME = "get";

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

        if (!StoreFile.exists()) {
            console.print(NO_DATA_FILE_MESSAGE);
            return;
        }

        try (final Password storePassword = readStorePassword(console)) {

            final Safe safe = new Safe(StoreFile.openWithPassword(storePassword));
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
            console.print("get usage: system user");
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }
}

