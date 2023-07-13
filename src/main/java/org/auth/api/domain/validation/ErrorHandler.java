package org.auth.api.domain.validation;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private final List<Error> errors;

    private ErrorHandler(final List<Error> errors) {
        this.errors = errors;
    }

    public static ErrorHandler create() {
        return new ErrorHandler(new ArrayList<>());
    }

    public ErrorHandler append(final Error error) {
        errors.add(error);
        return this;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public List<Error> getErrors() {
        return errors;
    }
}
