package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class PutCommand extends Command {

    static final String NAME = "put";
    static final String USAGE = "put usage: system user";

    private final String[] args;

    private Console console;
    private Login login;

    PutCommand(final String... args) {

        this.args = args;
    }

    @Override
    public void run(final Console console) {

        this.console = console;

        if (!parseArguments()) return;

        try (final Password password = readPassword(console);
             final Password storePassword = readStorePassword(console)) {

            new Safe(new FileStore(Paths.get(STORE_FILE_NAME).toFile(), storePassword))
                    .store(login, password);
        } catch (DecryptionFailed e) {
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

    private Password readPassword(final Console console) {

        console.print("Password for " + displayText(login) + ":");
        return new Password(console.readPassword());
    }
}
