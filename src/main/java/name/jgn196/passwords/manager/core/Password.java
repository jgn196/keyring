package name.jgn196.passwords.manager.core;

import java.util.Arrays;

import static java.util.Arrays.fill;

public class Password implements AutoCloseable {

    private final char[] chars;
    private boolean closed = false;

    public static Password from(final String secret) {

        return new Password(secret.toCharArray());
    }

    public Password(final char[] chars) {

        this.chars = chars;
    }

    public char[] characters() {

        if (closed) throw new IllegalStateException("Password is closed.");

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

    @Override
    public void close() {

        fill(chars, ' ');
    }
}
