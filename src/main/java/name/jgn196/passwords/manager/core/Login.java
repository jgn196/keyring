package name.jgn196.passwords.manager.core;

public class Login {

    private final String secureSystem;
    private final String userName;

    public Login(final String secureSystem, final String userName) {

        this.secureSystem = secureSystem;
        this.userName = userName;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Login)) return false;

        final Login other = (Login) obj;
        return secureSystem.equals(other.secureSystem) && userName.equals(other.userName);
    }
}
