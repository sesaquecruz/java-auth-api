package org.auth.api.application.user.update;

import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.exceptions.notification.IdentifierException;
import org.auth.api.domain.exceptions.notification.NotFoundException;
import org.auth.api.domain.exceptions.notification.NotificationException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.validation.Error;
import org.auth.api.domain.validation.ErrorHandler;
import org.auth.api.domain.validation.Notification;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Identifier;
import org.auth.api.domain.valueobjects.Password;

import java.util.Optional;

public class DefaultUpdateUser extends UpdateUser {
    public DefaultUpdateUser(final UserGateway userGateway) {
        super(userGateway);
    }

    @Override
    public Void execute(final UpdateUserInput input) {
        final var notification = Notification.create();

        final var id = createId(input.id(), notification);
        if (id.isEmpty())
            throw IdentifierException.with(notification);

        final var user = findUser(id.get());
        if (user.isEmpty())
            throw NotFoundException.with(User.class, id.get());

        final var email = createEmail(input.id(), input.email(), notification);
        final var password = createPassword(input.password(), notification);
        if (email.isEmpty() || password.isEmpty())
            throw NotificationException.with(notification);

        final var updatedUser = user.get()
                .updateEmail(email.get())
                .updatePassword(password.get());

        return saveUser(updatedUser);
    }

    private Optional<Identifier> createId(final String id, final Notification notification) {
        try {
            return Optional.of(Identifier.with(id));
        } catch (final ValidationException ex) {
            notification.append("id", ex.getErrorHandler());
            return Optional.empty();
        }
    }

    private Optional<User> findUser(final Identifier id) {
        try {
            return userGateway.findById(id);
        } catch (final Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }
    }

    private Optional<Email> createEmail(final String id, final String email, final Notification notification) {
        Email newEmail;
        try {
            newEmail = Email.with(email);
        } catch (final ValidationException ex) {
            notification.append("email", ex.getErrorHandler());
            return Optional.empty();
        }

        Optional<User> emailOwner;
        try {
            emailOwner = userGateway.findByEmail(newEmail);
        } catch (final Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }

        if (emailOwner.isPresent() && !id.equals(emailOwner.get().getId().getValue())) {
            final var errorHandler = ErrorHandler.create()
                    .append(Error.with("email already used"));
            notification.append("email", errorHandler);
            return Optional.empty();
        }

        return Optional.of(newEmail);
    }

    private Optional<Password> createPassword(final String password, final Notification notification) {
        try {
            return Optional.of(Password.withRawValue(password));
        } catch (final ValidationException ex) {
            notification.append("password", ex.getErrorHandler());
            return Optional.empty();
        }
    }

    private Void saveUser(final User user) {
        try {
            userGateway.save(user);
        } catch (final Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }
        return null;
    }
}
