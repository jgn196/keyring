package name.jgn196.passwords.manager;

import java.util.ResourceBundle;

class Application {

    static String version() {

        return ResourceBundle.getBundle("keyring").getString("application.version");
    }
}
