package org.auth.api.domain.valueobjects;

import org.auth.api.domain.ValueObject;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.IDUtils;
import org.auth.api.domain.validation.Error;
import org.auth.api.domain.validation.ErrorHandler;

import java.util.Objects;

public class Identifier extends ValueObject {
    private final String value;

    private Identifier(final String value) {
        this.value = value;
    }

    public static Identifier with(final String value) {
        final var handler = ErrorHandler.create();

        if (value == null) {
            handler.append(Error.with("id must not be null"));
            throw ValidationException.with(handler);
        }

        if (value.isBlank()) {
            handler.append(Error.with("id must not be empty"));
            throw ValidationException.with(handler);
        }

        if (!IDUtils.isUUID(value)) {
            handler.append(Error.with("id is invalid"));
            throw ValidationException.with(handler);
        }

        return new Identifier(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Identifier that = (Identifier) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
