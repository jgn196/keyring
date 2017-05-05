package name.jgn196.passwords.manager.crypto;

public interface StoreEncryption extends AutoCloseable {

    byte[] encrypt(byte[] plainText);
    byte[] encryptWithSalt(final byte[] plainText, final Salt salt);
    byte[] decrypt(byte[] encryptedData);
    byte[] decryptWithSalt(final Salt salt, final byte[] cipherText);
}
