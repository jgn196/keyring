package name.jgn196.passwords.manager.storage;

public interface StoreEncryption extends AutoCloseable {

    byte[] encrypt(byte[] plainText);
    byte[] decrypt(byte[] encryptedData);
}
