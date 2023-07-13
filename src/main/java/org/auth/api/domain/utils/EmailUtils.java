package org.auth.api.domain.utils;

import java.util.regex.Pattern;

public final class EmailUtils {
    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_]+(?:\\.[a-zA-Z0-9_]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private EmailUtils() { }

    public static boolean isValidEmail(final String address) {
        return emailPattern.matcher(address).matches();
    }
}
