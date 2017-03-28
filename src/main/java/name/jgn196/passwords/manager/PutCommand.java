package name.jgn196.passwords.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

class PutCommand extends Command {

    @Override
    public void run() {
        try {

            System.out.print("Password for Bill @ www.site.com:");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.print("Password for store:");
            if (!Paths.get("passwords.dat").toFile().createNewFile())
                throw new IOException("Failed to create password store.");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
