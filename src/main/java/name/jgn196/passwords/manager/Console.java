package name.jgn196.passwords.manager;

class Console {

    void printLine(final String text) {

        print(text + System.lineSeparator());
    }

    void print(final String text) {

        System.console().printf(text);
    }

    char[] readPassword() {

        return System.console().readPassword();
    }
}