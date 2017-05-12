package name.jgn196.passwords.manager.crypto;

import name.jgn196.passwords.manager.core.Password;

public interface StoreEncryption {

    byte[] encryptWithSalt(byte[] plainText, Salt salt, Password password);
    byte[] decryptWithSalt(byte[] cipherText, Salt salt, Password password);
}
