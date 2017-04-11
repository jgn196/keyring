package name.jgn196.passwords.manager.core;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class ALogin {

    @Test
    public void honoursTheEqualsContract() {

        EqualsVerifier.forClass(Login.class)
                .verify();
    }

    @Test
    public void includesTheSystemNameInToString() {

        assertThat(new Login("system", "ignored").toString(), containsString("system"));
    }

    @Test
    public void includesTheUserNameInToString() {

        assertThat(new Login("ignored", "user").toString(), containsString("user"));
    }
}
