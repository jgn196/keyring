package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.PasswordNotChanged;
import name.jgn196.passwords.manager.core.Safe;
import name.jgn196.passwords.manager.core.StoreFile;
import name.jgn196.passwords.manager.crypto.DecryptionFailed;

class ChangePasswordCommand extends Command {

    static final String NAME = "change_password";

    ChangePasswordCommand(final Console console, final StoreFile storeFile) {

        super(console, storeFile);
    }

    @Override
    void run() {

        if (!checkStoreExists()) return;

        changePassword();
    }

    private void changePassword() {
        try (final Password storePassword = readStorePassword();
             final Password newPassword = readPasswordFromConsole("New store password:");
             final Password confirmedPassword = readPasswordFromConsole("Confirm new store password:");
             final Safe safe = new Safe(storeFile(), storePassword)) {

            if (newPassword.equals(confirmedPassword)) {
                safe.changePasswordTo(newPassword);
                printLineToConsole("Store password changed");
            } else
                printLineToConsole("New store passwords do not match.");

        } catch (PasswordNotChanged e) {

            if (e.getCause() instanceof DecryptionFailed) printLineToConsole(INCORRECT_STORE_PASSWORD);
        }
    }
}
