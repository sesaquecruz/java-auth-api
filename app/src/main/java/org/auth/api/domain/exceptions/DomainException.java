package org.auth.api.domain.exceptions;

public abstract class DomainException extends RuntimeException {
    public DomainException(final String message) {
        super(message, null, true, false);
    }

    public DomainException() {
        this(null);
    }
}
