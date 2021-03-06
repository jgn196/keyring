package name.jgn196.passwords.manager.core;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class APassword {

    @Test
    public void doesNotCopyPasswordCharacters() {

        final char[] passwordData = "secret".toCharArray();

        assertSame(passwordData, new Password(passwordData).characters());
    }

    @Test
    public void wipesItsCharactersWhenItCloses() {

        final char[] passwordData = "secret".toCharArray();

        new Password(passwordData).close();

        assertArrayEquals(new char[]{' ', ' ', ' ', ' ', ' ', ' '}, passwordData);
    }

    @Test(expected = IllegalStateException.class)
    public void doesNotProvideCharactersAfterItIsClosed() {

        final Password password = Password.from("secret");
        password.close();

        password.characters();
    }

    @Test
    public void honoursEqualsContract() {

        EqualsVerifier.forClass(Password.class)
                .withIgnoredFields("closed")
                .verify();
    }

    @Test
    public void doesNotIncludeTheCharactersInToStringOutput() {

        assertThat(Password.from("secret").toString(), containsString("******"));
    }

    @Test
    public void toStringTellsYouIfItIsClosed() {

        final Password password = Password.from("secret");

        assertThat(password.toString(), not(containsString("closed")));

        password.close();

        assertThat(password.toString(), containsString("closed"));
    }
}
