package name.jgn196.passwords.manager.core;

public class Login {

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
        return secureSystem.equals(other.secureSystem) && userName.equals(other.userName);
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + secureSystem.hashCode();
        result = 31 * result + userName.hashCode();
        return result;
    }

    @Override
    public String toString() {

        return "Login ('" + userName + "' @ '" + secureSystem + "')";
    }

    public String displayText() {

        return userName + " @ " + secureSystem;
    }
}
