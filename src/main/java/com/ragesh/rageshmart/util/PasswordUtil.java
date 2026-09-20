package com.ragesh.rageshmart.util;

import org.mindrot.jbcrypt.BCrypt;

/** Section 2, rule 2: bcrypt only, no plaintext, no MD5/SHA1-only hashing. */
public final class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    private PasswordUtil() { }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verify(String plainPassword, String hash) {
        if (plainPassword == null || hash == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, hash);
        } catch (IllegalArgumentException e) {
            // malformed hash in storage - treat as failed auth, never throw to caller
            return false;
        }
    }
}
