package org.auth.api.domain.valueobjects;

import org.auth.api.domain.ValueObject;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.PasswordUtils;
import org.auth.api.domain.validation.Error;
import org.auth.api.domain.validation.ErrorHandler;

import java.util.Objects;

public class Password extends ValueObject {
    private final String value;

    private Password(final String value) {
        this.value = value;
    }

    public static Password withRawValue(final String value) {
        final var handler = ErrorHandler.create();

        if (value == null) {
            handler.append(Error.with("password must not be null"));
            throw ValidationException.with(handler);
        }

        if (value.isBlank()) {
            handler.append(Error.with("password must not be empty"));
            throw ValidationException.with(handler);
        }

        final var strippedValue = value.strip();

        if (strippedValue.length() < 6) {
            handler.append(Error.with("password must have more than 5 characters"));
            throw ValidationException.with(handler);
        }

        if (strippedValue.length() > 100) {
            handler.append(Error.with("password must not have more than 100 characters"));
            throw ValidationException.with(handler);
        }

        return new Password(PasswordUtils.encodePassword(strippedValue));
    }

    public static Password withEncodedValue(final String value) {
        if (!PasswordUtils.isEncodedPassword(value)) {
            final var handler = ErrorHandler.create()
                    .append(Error.with("password must be encoded"));
            throw ValidationException.with(handler);
        }
        return new Password(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Password password = (Password) o;
        return Objects.equals(getValue(), password.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
