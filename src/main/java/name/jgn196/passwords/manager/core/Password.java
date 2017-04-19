package name.jgn196.passwords.manager.core;

import java.util.Arrays;

import static java.util.Arrays.fill;

public final class Password implements AutoCloseable {

    private final char[] value;
    private boolean closed = false;

    public static Password from(final String secret) {

        return new Password(secret.toCharArray());
    }

    public Password(final char[] value) {

        this.value = value;
    }

    public char[] characters() {

        if (closed) throw new IllegalStateException("Password is closed.");

        return value;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) return true;
        if (! (obj instanceof Password)) return false;

        return Arrays.equals(value, ((Password) obj).value);
    }

    @Override
    public int hashCode() {

        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {

        return "Password ("+ maskedPassword() +")" + (closed ? " [closed]" : "");
    }

    private String maskedPassword() {

        final char[] maskedPassword = new char[value.length];
        fill(maskedPassword, '*');

        return new String(maskedPassword);
    }

    @Override
    public void close() {

        fill(value, ' ');
        closed = true;
    }

    public boolean isClosed() {

        return closed;
    }
}
