package org.auth.api.domain.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationHandler {
    private final List<ValidationError> errors;

    private ValidationHandler(final List<ValidationError> errors) {
        this.errors = errors;
    }

    public static ValidationHandler create() {
        return new ValidationHandler(new ArrayList<>());
    }

    public ValidationHandler append(final ValidationError error) {
        errors.add(error);
        return this;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
