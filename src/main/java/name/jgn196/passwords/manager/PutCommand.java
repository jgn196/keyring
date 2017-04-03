package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class PutCommand extends Command {

    static final String NAME = "put";

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
             final Password storePassword = readStorePassword(console);
             final Safe safe = new Safe(new FileStore(Paths.get(STORE_FILE_NAME).toFile(), storePassword))) {

            safe.store(login, password);
        }
    }

    private boolean parseArguments() {

        if (args.length < 2) {

            console.print("put usage: system user");
            return false;
        }
        login = new Login(args[1], args[2]);
        return true;
    }

    private Password readPassword(final Console console) {

        console.print("Password for " + login.displayText() + ":");
        return new Password(console.readPassword());
    }
}
