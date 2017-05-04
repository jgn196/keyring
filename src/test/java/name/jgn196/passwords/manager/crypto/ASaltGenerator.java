package name.jgn196.passwords.manager.crypto;

import org.junit.Test;

import static name.jgn196.passwords.manager.crypto.ASalt.isSalt;
import static org.junit.Assert.assertTrue;

public class ASaltGenerator {

    @Test
    public void canBeGenerated() {

        final Salt salt = new SaltGenerator().get();

        assertTrue(isSalt(salt.toBytes()));
    }
}
