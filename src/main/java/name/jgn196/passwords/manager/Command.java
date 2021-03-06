package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;
import name.jgn196.passwords.manager.core.Password;
import name.jgn196.passwords.manager.core.StoreFile;

abstract class Command {

    static final String NO_DATA_FILE_MESSAGE = "No data file.";
    static final String INCORRECT_STORE_PASSWORD = "Incorrect store password.";
    static final String USER_ARGUMENT_HELP = "\tuser\tThe user name in the secure system";
    static final String SYSTEM_ARGUMENT_HELP = "\tsystem\tThe name of the secure system";

    private final Console console;
    private final StoreFile storeFile;

    Command(final Console console, final StoreFile storeFile) {

        this.console = console;
        this.storeFile = storeFile;
    }

    abstract void run();

    Password readStorePassword() {

        return readPasswordFromConsole("Password for store:");
    }

    Password readPasswordFromConsole(final String prompt) {

        console.print(prompt);

        return new Password(console.readPassword());
    }

    void printLineToConsole(final String message) {

        console.printLine(message);
    }

    static String displayText(final Login login) {

        return login.userName() + " @ " + login.secureSystem();
    }

    boolean checkStoreExists() {

        final boolean storeExists = storeFile.exists();
        if (!storeExists) printLineToConsole(NO_DATA_FILE_MESSAGE);

        return storeExists;
    }

    StoreFile storeFile() { return storeFile; }

}

