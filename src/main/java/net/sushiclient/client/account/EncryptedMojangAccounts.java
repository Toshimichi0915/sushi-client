package net.sushiclient.client.account;

import org.apache.commons.lang3.RandomUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptedMojangAccounts extends PlainMojangAccounts {

    public static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final int KEY_LENGTH = 16;
    public static final int IV_LENGTH = 16;

    public EncryptedMojangAccounts(File file) {
        super(file);
    }

    public EncryptedMojangAccounts(MojangAPI mojangApi, File file) {
        super(mojangApi, file);
    }

    @Override
    protected String serialize(MojangAccount acc) {
        String line = super.serialize(acc);

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM.split("/")[0]);
            keyGenerator.init(KEY_LENGTH << 3);
            SecretKey key = keyGenerator.generateKey();

            IvParameterSpec iv = new IvParameterSpec(RandomUtils.nextBytes(IV_LENGTH));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] bytes = cipher.doFinal(line.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[KEY_LENGTH + IV_LENGTH + bytes.length];
            System.arraycopy(key.getEncoded(), 0, result, 0, key.getEncoded().length);
            System.arraycopy(iv.getIV(), 0, result, KEY_LENGTH, iv.getIV().length);
            System.arraycopy(bytes, 0, result, KEY_LENGTH + IV_LENGTH, bytes.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected MojangAccount deserialize(String line) {
        try {
            byte[] bytes = Base64.getDecoder().decode(line);
            byte[] keyBytes = new byte[KEY_LENGTH];
            byte[] ivBytes = new byte[IV_LENGTH];
            byte[] contentsBytes = new byte[bytes.length - KEY_LENGTH - IV_LENGTH];
            System.arraycopy(bytes, 0, keyBytes, 0, keyBytes.length);
            System.arraycopy(bytes, KEY_LENGTH, ivBytes, 0, ivBytes.length);
            System.arraycopy(bytes, KEY_LENGTH + IV_LENGTH, contentsBytes, 0, contentsBytes.length);

            SecretKeySpec key = new SecretKeySpec(keyBytes, ALGORITHM.split("/")[0]);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return super.deserialize(new String(cipher.doFinal(contentsBytes), StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }
}
