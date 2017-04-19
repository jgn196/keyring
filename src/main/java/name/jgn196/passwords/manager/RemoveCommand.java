package name.jgn196.passwords.manager;

class RemoveCommand extends Command {

    static final String NAME = "";

    RemoveCommand(final String... args) {

    }

    @Override
    void run(Console console) {

        console.print("remove usage: system user");
    }
}
