package name.jgn196.passwords.manager;

import name.jgn196.passwords.manager.core.Password;

import java.io.IOException;
import java.nio.file.Paths;

class PutCommand extends Command {

    @Override
    public void run(final Console console) {
        try (final Password password = readPassword(console);
             final Password storePassword = readStorePassword(console)){

            if (!Paths.get("passwords.dat").toFile().createNewFile())
                throw new IOException("Failed to create password store.");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private Password readPassword(final Console console) {

        console.print("Password for Bill @ www.site.com:");
        return new Password(console.readPassword());
    }

    private Password readStorePassword(final Console console) {

        console.print("Password for store:");
        return new Password(console.readPassword());
    }
}
