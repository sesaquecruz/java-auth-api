package org.auth.api.domain.exceptions;

public class InternalErrorException extends RuntimeException {
    private InternalErrorException(final String message, final Throwable cause) {
        super(message, cause, true, false);
    }

    public static InternalErrorException with(final String message, final Throwable cause) {
        return new InternalErrorException(message, cause);
    }
}
