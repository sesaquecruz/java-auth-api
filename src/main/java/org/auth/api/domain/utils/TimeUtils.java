package org.auth.api.domain.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class TimeUtils {
    private TimeUtils() { }

    public static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }
}
