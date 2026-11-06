package rentit;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private static final String PREFIX = "SHA-256:";

    private PasswordUtil() {
        // Utility class - no objects needed.
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            password.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                String value = Integer.toHexString(b & 0xff);

                if (value.length() == 1) {
                    hex.append('0');
                }

                hex.append(value);
            }

            return PREFIX + hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    e
            );
        }
    }

    public static boolean isHashed(String password) {
        return password != null
                && password.startsWith(PREFIX);
    }
}