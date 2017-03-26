package name.jgn196.passwords.manager;

public class Manager {

    public static void main(final String... args) {

        if (args.length > 0 && args[0].equals("list"))
            System.out.print("No data file.");
        else
            System.out.print("Commands:\n" +
            "\tlist\tLists all the stored logins.");
    }
}
