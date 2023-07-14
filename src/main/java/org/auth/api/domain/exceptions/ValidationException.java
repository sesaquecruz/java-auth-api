package org.auth.api.domain.exceptions;

import org.auth.api.domain.validation.ErrorHandler;

public class ValidationException extends DomainException {
    private final ErrorHandler errorHandler;

    private ValidationException(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public static ValidationException with(final ErrorHandler errorHandler) {
        return new ValidationException(errorHandler);
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
}
