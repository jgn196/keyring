package name.jgn196.passwords.manager.core;

import java.util.Arrays;

import static java.util.Arrays.fill;

public class Password {

    private final char[] chars;

    public static Password from(final String secret) {

        return new Password(secret.toCharArray());
    }

    public Password(final char[] chars) {

        this.chars = chars;
    }

    public char[] characters() {

        return chars;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) return true;
        if (! (obj instanceof Password)) return false;

        return Arrays.equals(chars, ((Password) obj).chars);
    }

    @Override
    public int hashCode() {

        return Arrays.hashCode(chars);
    }

    @Override
    public String toString() {

        final char[] maskedPassword = new char[chars.length];
        fill(maskedPassword, '*');

        return "Password ("+ new String(maskedPassword) +")";
    }
}
