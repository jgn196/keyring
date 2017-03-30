package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class ListCommand extends Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file."; // TODO - Move to base class

    @Override
    public void run(final Console console) {

        if (!Paths.get(STORE_FILE_NAME).toFile().exists()) {
            console.print(NO_DATA_FILE_MESSAGE);
            return;
        }

        try (final Password storePassword = readStorePassword(console);
             final Safe safe = new Safe(new FileStore(Paths.get(STORE_FILE_NAME).toFile(), storePassword))) {

            console.print("Passwords stored for:\n");
            safe.logins()
                    .forEach(login -> console.print("\t" + login.displayText() + "\n"));
        }
    }
}
