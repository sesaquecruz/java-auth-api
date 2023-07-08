package org.auth.api.domain.exceptions;

import org.auth.api.domain.validation.ValidationError;
import org.auth.api.domain.validation.ValidationHandler;

import java.util.List;

public class ValidationException extends DomainException {
    private final List<ValidationError> errors;

    private ValidationException(final List<ValidationError> errors) {
        this.errors = errors;
    }

    public static ValidationException with(final ValidationHandler handler) {
        return new ValidationException(handler.getErrors());
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
