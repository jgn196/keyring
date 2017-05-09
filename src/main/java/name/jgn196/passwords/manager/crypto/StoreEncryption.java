package name.jgn196.passwords.manager.crypto;

import name.jgn196.passwords.manager.core.Password;

public interface StoreEncryption extends AutoCloseable {

    byte[] encryptWithSalt(byte[] plainText, Salt salt);
    byte[] decryptWithSalt(byte[] cipherText, Salt salt);
    void changePassword(Password password);
}
