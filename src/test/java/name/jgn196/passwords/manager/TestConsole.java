package name.jgn196.passwords.manager;

class TestConsole implements Console {

    private final StringBuffer capturedOutput = new StringBuffer();

    private int nextInput = 0;
    private String[] inputs = new String[0];

    @Override
    public void print(final String text) {

        capturedOutput.append(text);
    }

    @Override
    public char[] readPassword() {

        return nextInput < inputs.length ? inputs[nextInput++].toCharArray() : new char[0];
    }

    String capturedOutput() {

        return capturedOutput.toString();
    }

    void prepareInput(final String... inputs) {

        this.inputs = inputs;
        nextInput = 0;
    }
}