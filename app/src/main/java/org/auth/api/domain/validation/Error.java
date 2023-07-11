package org.auth.api.domain.validation;

public record Error(
        String message
) {
    public static Error with(final String message) {
        return new Error(message);
    }
}
