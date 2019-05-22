package security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import play.Logger;

/**
 * @author Max Meijer
 * Created on 20/03/2019
 */
public class PasswordHelper {
    private static Logger.ALogger logger = Logger.of(PasswordHelper.class);

    private PasswordHelper() {}

    public static byte[] generateSalt() {
        logger.debug("Generating salt");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        logger.debug("Generated salt: " + Arrays.toString(salt));
        return salt;
    }

    public static byte[] generateHash(byte[] salt, String plainPassword) {
        logger.debug("Hashing password with salt: " + Arrays.toString(salt));
        KeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(e.getMessage());
        }

        return new byte[0];
    }
}
