package name.jgn196.passwords.manager.core;

public final class Login {

    private final String secureSystem;
    private final String userName;

    public Login(final String secureSystem, final String userName) {

        this.secureSystem = secureSystem;
        this.userName = userName;
    }

    public String secureSystem() {

        return secureSystem;
    }

    public String userName() {

        return userName;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Login)) return false;

        final Login other = (Login) obj;
        final boolean systemsEqual = secureSystem == null ?
                other.secureSystem == null : secureSystem.equals(other.secureSystem);
        final boolean usersEqual = userName == null ? other.userName == null : userName.equals(other.userName);
        return systemsEqual && usersEqual;
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + (secureSystem == null ? 0 : secureSystem.hashCode());
        result = 31 * result + (userName == null ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public String toString() {

        return "Login ('" + userName + "' @ '" + secureSystem + "')";
    }

}
