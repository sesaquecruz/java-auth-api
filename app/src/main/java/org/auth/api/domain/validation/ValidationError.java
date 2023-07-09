package org.auth.api.domain.validation;

public record ValidationError(
        String message
) {
    public static ValidationError with(final String message) {
        return new ValidationError(message);
    }
}
