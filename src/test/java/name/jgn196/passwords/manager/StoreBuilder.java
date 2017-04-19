package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Login;

import java.io.IOException;

class StoreBuilder {

    private final TestConsole console;
    private final String storePassword;

    StoreBuilder(final TestConsole console, final String storePassword) {

        this.console = console;
        this.storePassword = storePassword;
    }

    void containing(final Login login, final String password) throws IOException {

        console.prepareInput(password, storePassword);
        Manager.main(PutCommand.NAME, login.secureSystem(), login.userName());
    }
}
