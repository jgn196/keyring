package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.storage.DecryptionFailed;

class ListCommand extends Command {

    static final String NAME = "list";

    @Override
    public void run(final Console console) {

        if (!checkStoreExists(console)) return;

        try (final Password storePassword = readStorePassword(console)) {

            console.print("Passwords stored for:\n");
            new Safe(StoreFile.openWithPassword(storePassword))
                    .logins()
                    .forEach(login -> console.print("\t" + displayText(login) + "\n"));
        } catch(DecryptionFailed e) {

            console.print(INCORRECT_STORE_PASSWORD);
        }
    }
}
