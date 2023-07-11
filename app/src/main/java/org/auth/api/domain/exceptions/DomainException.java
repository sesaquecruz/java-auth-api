package org.auth.api.domain.exceptions;

public abstract class DomainException extends RuntimeException {
    protected DomainException(final String message) {
        super(message, null, true, false);
    }

    protected DomainException() {
        this(null);
    }
}
