package name.jgn196.passwords.manager;

class Console {

    void print(final String text) {

        System.console().printf(text);
    }

    char[] readPassword() {

        return System.console().readPassword();
    }
}