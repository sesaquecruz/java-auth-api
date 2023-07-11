package org.auth.api.domain.utils;

import java.util.UUID;

public final class IDUtils {
    private IDUtils() { }

    public static String newUUID() {
        return UUID.randomUUID().toString().toLowerCase();
    }

    public static boolean isUUID(final String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }
}
