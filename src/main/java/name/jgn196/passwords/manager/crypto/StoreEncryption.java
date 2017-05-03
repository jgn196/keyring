package name.jgn196.passwords.manager.crypto;

public interface StoreEncryption extends AutoCloseable {

    byte[] encrypt(byte[] plainText);
    byte[] decrypt(byte[] encryptedData);
}
