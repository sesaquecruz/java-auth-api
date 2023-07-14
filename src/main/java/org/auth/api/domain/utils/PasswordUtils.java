package org.auth.api.domain.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

public final class PasswordUtils {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final Pattern encoderPattern = Pattern.compile("^\\$2[ayb]\\$.{56}$");

    private PasswordUtils() { }

    public static String encodePassword(final String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean isEncodedPassword(final String encodedPassword) {
        return encoderPattern.matcher(encodedPassword).matches();
    }

    public static boolean verifyPassword(final String rawPassword, final String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
