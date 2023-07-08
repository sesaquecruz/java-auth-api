package org.auth.api.domain.valueobjects;

import org.auth.api.domain.ValueObject;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.EmailUtils;
import org.auth.api.domain.validation.ValidationError;
import org.auth.api.domain.validation.ValidationHandler;

import java.util.Objects;

public class Email extends ValueObject {
    private final String address;

    private Email(final String address) {
        this.address = address;
    }

    public static Email newEmail(final String address) {
        final var handler = ValidationHandler.create();

        if (address == null) {
            handler.append(ValidationError.with("email must not be null"));
            throw ValidationException.with(handler);
        }

        final var strippedAddress = address.strip();

        if (strippedAddress.isBlank())
            handler.append(ValidationError.with("email must not be empty"));

        if (!EmailUtils.isValidEmail(strippedAddress))
            handler.append(ValidationError.with("email is invalid"));

        if (strippedAddress.length() > 100)
            handler.append(ValidationError.with("email must not have more than 100 characters"));

        if (handler.hasError())
            throw ValidationException.with(handler);

        return new Email(strippedAddress);
    }

    public static Email with(final String address) {
        return new Email(address);
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
