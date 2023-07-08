package org.auth.api.domain.valueobjects;

import org.auth.api.domain.ValueObject;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.PasswordUtils;
import org.auth.api.domain.validation.ValidationError;
import org.auth.api.domain.validation.ValidationHandler;

import java.util.Objects;

public class Password extends ValueObject {
    private final String value;

    private Password(final String value) {
        this.value = value;
    }

    public static Password newPassword(final String value) {
        final var handler = ValidationHandler.create();

        if (value == null) {
            handler.append(ValidationError.with("password must not be null"));
            throw ValidationException.with(handler);
        }

        final var strippedValue = value.strip();

        if (strippedValue.isBlank())
            handler.append(ValidationError.with("password must not be empty"));

        if (strippedValue.length() < 6)
            handler.append(ValidationError.with("password must have more than 5 characters"));

        if (strippedValue.length() > 100)
            handler.append(ValidationError.with("password must not have more than 100 characters"));

        if (handler.hasError())
            throw ValidationException.with(handler);

        return new Password(PasswordUtils.encodePassword(strippedValue));
    }

    public static Password with(final String value) {
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
