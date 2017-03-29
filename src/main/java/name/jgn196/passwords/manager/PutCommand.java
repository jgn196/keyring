package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class PutCommand extends Command {

    @Override
    public void run(final Console console) {
        try (final Password password = readPassword(console);
             final Password storePassword = readStorePassword(console)) {

            new Safe(new FileStore(Paths.get(STORE_FILE_NAME).toFile(), storePassword))
                    .store(new Login("www.site.com", "Bill"), password);
        }
    }

    private Password readPassword(final Console console) {

        console.print("Password for Bill @ www.site.com:");
        return new Password(console.readPassword());
    }
}
