package name.jgn196.passwords.manager;

class Console {

    void printLine(final String text) {

        System.console().printf(text + System.lineSeparator());
    }

    char[] readPassword() {

        return System.console().readPassword();
    }
}