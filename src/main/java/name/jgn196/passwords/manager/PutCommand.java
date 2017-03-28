package name.jgn196.passwords.manager;

import java.io.IOException;
import java.nio.file.Paths;

class PutCommand extends Command {

    @Override
    public void run(final Console console) {
        try {

            console.print("Password for Bill @ www.site.com:");
            console.readPassword();
            console.print("Password for store:");
            if (!Paths.get("passwords.dat").toFile().createNewFile())
                throw new IOException("Failed to create password store.");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
