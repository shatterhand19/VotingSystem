package sequrity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by bozhidar on 08.11.17.
 */
public class Crypto {
    private static SecureRandom random = new SecureRandom();

    /**
     * Gets a String and returns a SHA-256 digest of it.
     *
     * @param msg is the string to be hashed
     * @return
     */
    public static byte[] hashSHA256(String msg) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return  sha256.digest(msgBytes);
        } catch (NoSuchAlgorithmException e) {
            //Not gonna happen
            e.printStackTrace();
            return new byte[1];
        }
    }

    /**
     * Gets a random secure long, used for nonce and hash
     * @return
     */
    public static long getSecureLong() {
        //Get nonce/salt
        return random.nextLong();
    }
}
