package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;
import name.jgn196.passwords.manager.storage.FileStore;

import java.nio.file.Paths;

import static name.jgn196.passwords.manager.Manager.STORE_FILE_NAME;

class ListCommand extends Command {

    static final String NAME = "list";

    @Override
    public void run(final Console console) {

        if (!Paths.get(STORE_FILE_NAME).toFile().exists()) {
            console.print(NO_DATA_FILE_MESSAGE);
            return;
        }

        try (final Password storePassword = readStorePassword(console)) {

            console.print("Passwords stored for:\n");
            new Safe(new FileStore(Paths.get(STORE_FILE_NAME).toFile(), storePassword))
                    .logins()
                    .forEach(login -> console.print("\t" + displayText(login) + "\n"));
        } catch(DecryptionFailed e) {

            console.print(INCORRECT_STORE_PASSWORD);
        }
    }
}
