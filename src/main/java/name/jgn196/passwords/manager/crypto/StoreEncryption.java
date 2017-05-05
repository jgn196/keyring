package name.jgn196.passwords.manager.crypto;

public interface StoreEncryption extends AutoCloseable {

    byte[] encryptWithSalt(final byte[] plainText, final Salt salt);
    byte[] decryptWithSalt(final byte[] cipherText, final Salt salt);
}
