package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.PasswordNotChanged;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.core.StoreFile;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;

class ChangePasswordCommand extends Command {

    ChangePasswordCommand(final Console console, final StoreFile storeFile) {

        super(console, storeFile);
    }

    @Override
    void run() {

        if (!checkStoreExists()) return;

        try (final Password storePassword = readStorePassword();
             final Password newPassword = readPasswordFromConsole("New store password:");
             final Safe safe = new Safe(storeFile(), storePassword)) {

            safe.changePasswordTo(newPassword);

        } catch (PasswordNotChanged e) {

            if (e.getCause() instanceof DecryptionFailed) printToConsole(INCORRECT_STORE_PASSWORD);
        }
    }
}
