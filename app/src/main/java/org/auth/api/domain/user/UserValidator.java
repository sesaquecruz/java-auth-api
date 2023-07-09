package org.auth.api.domain.user;

import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.validation.ValidationHandler;
import org.auth.api.domain.validation.Validation;
import org.auth.api.domain.validation.ValidationError;

public class UserValidator extends Validation<User> {
    private UserValidator(final User user) {
        super(user);
    }

    public static UserValidator with(final User user) {
        return new UserValidator(user);
    }

    @Override
    protected void validate() {
        final var handler = ValidationHandler.create();

        final var id = getEntity().getId();
        final var email = getEntity().getEmail();
        final var password = getEntity().getPassword();
        final var createdAt = getEntity().getCreatedAt();
        final var updatedAt = getEntity().getUpdatedAt();

        if (id == null)
            handler.append(ValidationError.with("id must not be null"));

        if (email == null)
            handler.append(ValidationError.with("email must not be null"));

        if (password == null)
            handler.append(ValidationError.with("password must not be null"));

        if (createdAt == null)
            handler.append(ValidationError.with("created at must not be null"));

        if (updatedAt == null)
            handler.append(ValidationError.with("updated at must not be null"));

        if (createdAt != null && updatedAt != null && updatedAt.isBefore(createdAt))
            handler.append(ValidationError.with("update at must not come before created at"));

        if (handler.hasError())
            throw ValidationException.with(handler);
    }
}
