package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;
import java.util.Optional;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class GetCommand extends Command {

    private final Login login;

    GetCommand(final String[] args) {

        login = new Login(args[1], args[2]);
    }

    @Override
    void run(final Console console) {

        if (!Paths.get(STORE_FILE_NAME).toFile().exists()) {
            new NoDataFileCommand().run(console);
            return;
        }

        try (final Password storePassword = readStorePassword(console);
             final Safe safe = new Safe(new FileStore(Paths.get(STORE_FILE_NAME).toFile(), storePassword))) {

            final Optional<Password> password = safe.passwordFor(login);

            if (password.isPresent())
                console.print(new String(password.get().characters()));
            else
                console.print("Password for " + login.displayText() + " not found.");
        }
    }
}
