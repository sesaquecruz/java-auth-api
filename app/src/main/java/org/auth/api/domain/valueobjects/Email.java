package org.auth.api.domain.valueobjects;

import org.auth.api.domain.ValueObject;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.EmailUtils;
import org.auth.api.domain.validation.Error;
import org.auth.api.domain.validation.ErrorHandler;

import java.util.Objects;

public class Email extends ValueObject {
    private final String address;

    private Email(final String address) {
        this.address = address;
    }

    public static Email with(final String address) {
        final var handler = ErrorHandler.create();

        if (address == null) {
            handler.append(Error.with("email must not be null"));
            throw ValidationException.with(handler);
        }

        if (address.isBlank()) {
            handler.append(Error.with("email must not be empty"));
            throw ValidationException.with(handler);
        }

        final var strippedAddress = address.strip();

        if (strippedAddress.length() > 100) {
            handler.append(Error.with("email must not have more than 100 characters"));
            throw ValidationException.with(handler);
        }

        if (!EmailUtils.isValidEmail(strippedAddress)) {
            handler.append(Error.with("email is invalid"));
            throw ValidationException.with(handler);
        }

        return new Email(strippedAddress);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Email email = (Email) o;
        return Objects.equals(getAddress(), email.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress());
    }
}
