package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.storage.FileStore;

abstract class Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";
    static final String INCORRECT_STORE_PASSWORD = "Incorrect store password.";

    private final Console console;
    private final StoreFile storeFile;

    protected Command(final Console console, final StoreFile storeFile) {

        this.console = console;
        this.storeFile = storeFile;
    }

    Password readStorePassword() {

        consolePrint("Password for store:");
        return new Password(consoleReadPassword());
    }

    void consolePrint(final String message) {

        console.print(message);
    }

    char[] consoleReadPassword() {

        return console.readPassword();
    }

    static String displayText(final Login login) {

        return login.userName() + " @ " + login.secureSystem();
    }

    abstract void run();

    boolean checkStoreExists() {

        if (!storeFile.exists()) consolePrint(NO_DATA_FILE_MESSAGE);
        return storeFile.exists();
    }

    FileStore openWithPassword(final Password password) {

        return storeFile.openWithPassword(password);
    }
}

