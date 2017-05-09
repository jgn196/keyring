package name.jgn196.passwords.manager.crypto;

import name.jgn196.passwords.manager.core.Password;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import static java.util.Arrays.copyOf;

public class SaltedAesEncryption implements StoreEncryption {

    private static final int ITERATIONS = 20;
    private static final boolean ENCRYPTING = true;
    private static final boolean DECRYPTING = false;

    private Password password;

    public SaltedAesEncryption(final Password password) {

        this.password = password;
    }

    @Override
    public byte[] encryptWithSalt(final byte[] plainText, final Salt salt) {
        try {

            final PKCS12ParametersGenerator parametersGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
            parametersGenerator.init(
                    PBEParametersGenerator.PKCS12PasswordToBytes(password.characters()),
                    salt.toBytes(),
                    ITERATIONS);

            final CBCBlockCipher blockCipher = new CBCBlockCipher(new AESEngine());

            final PaddedBufferedBlockCipher paddedCipher = new PaddedBufferedBlockCipher(blockCipher, new PKCS7Padding());
            paddedCipher.init(ENCRYPTING, parametersGenerator.generateDerivedParameters(256, 128));

            final byte[] result = new byte[paddedCipher.getOutputSize(plainText.length)];

            int bytesCopied = paddedCipher.processBytes(plainText, 0, plainText.length, result, 0);
            paddedCipher.doFinal(result, bytesCopied);

            return result;
        } catch(InvalidCipherTextException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decryptWithSalt(final byte[] cipherText, final Salt salt) {
        try {

            final PKCS12ParametersGenerator parametersGenerator = new PKCS12ParametersGenerator(new SHA256Digest());
            parametersGenerator.init(
                    PBEParametersGenerator.PKCS12PasswordToBytes(password.characters()),
                    salt.toBytes(),
                    ITERATIONS);

            final CBCBlockCipher blockCipher = new CBCBlockCipher(new AESEngine());

            final PaddedBufferedBlockCipher paddedCipher = new PaddedBufferedBlockCipher(blockCipher, new PKCS7Padding());
            paddedCipher.init(DECRYPTING, parametersGenerator.generateDerivedParameters(256, 128));

            final byte[] buffer = new byte[paddedCipher.getOutputSize(cipherText.length)];

            int bytesCopied = paddedCipher.processBytes(cipherText, 0, cipherText.length, buffer, 0);
            int plainTextLength = bytesCopied + paddedCipher.doFinal(buffer, bytesCopied);

            return copyOf(buffer, plainTextLength);
        } catch(InvalidCipherTextException e) {

            throw new DecryptionFailed(e);
        }
    }

    @Override
    public void changePassword(final Password password) {

        this.password.close();
        this.password = password;
    }

    @Override
    public void close() {

        password.close();
    }
}