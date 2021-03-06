package security;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


/**
 * @author Max Meijer
 * Created on 26/03/2019
 */
public class PasswordHelperTest {

    @Test
    public void generateSalt() {

        byte[] firstSalt = PasswordHelper.generateSalt();
        byte[] secondSalt = PasswordHelper.generateSalt();

        assertNotNull(firstSalt);
        assertNotNull(secondSalt);

        assertEquals(16 , firstSalt.length);
        assertEquals(16 , secondSalt.length);

        assertNotEquals(firstSalt, secondSalt);
    }

    @Test
    public void generateHash() {
        byte[] salt = PasswordHelper.generateSalt();
        byte[] salt1 = PasswordHelper.generateSalt();

        byte[] password = PasswordHelper.generateHash(salt, "test");
        byte[] password1 = PasswordHelper.generateHash(salt1, "test");

        assertNotNull(password);
        assertNotNull(password1);

        assertNotEquals(password, password1);
    }


    @Test
    public void generateSameHash() {
        final String passwordText = "password";
        byte[] salt = PasswordHelper.generateSalt();

        byte[] password = PasswordHelper.generateHash(salt, passwordText);
        byte[] password1 = PasswordHelper.generateHash(salt, passwordText);

        assertNotNull(password);
        assertNotNull(password1);

        assertEquals(Arrays.toString(password), Arrays.toString(password1));
    }
}
