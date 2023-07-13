package org.auth.api.application.user.create;

import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.notification.NotificationException;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.validation.ErrorHandler;
import org.auth.api.domain.validation.Notification;
import org.auth.api.domain.validation.Error;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Password;

import java.util.Optional;

public class DefaultCreateUser extends CreateUser {
    public DefaultCreateUser(final UserGateway userGateway) {
        super(userGateway);
    }

    @Override
    public CreateUserOutput execute(final CreateUserInput input) {
        final var notification = Notification.create();

        final var email = createEmail(input.email(), notification);
        final var password = createPassword(input.password(), notification);

        if (email.isEmpty() || password.isEmpty())
            throw NotificationException.with(notification);

        if (findUser(email.get(), notification).isPresent())
            throw NotificationException.with(notification);

        final var user = createUser(email.get(), password.get(), notification);
        if (user.isEmpty())
            throw NotificationException.with(notification);

        return createOutput(saveUser(user.get()));
    }

    private Optional<Email> createEmail(final String email, final Notification notification) {
        try {
            return Optional.of(Email.with(email));
        } catch (final ValidationException ex) {
            notification.append("email", ex.getErrorHandler());
            return Optional.empty();
        }
    }

    private Optional<Password> createPassword(final String password, final Notification notification) {
        try {
            return Optional.of(Password.withRawValue(password));
        } catch (final ValidationException ex) {
            notification.append("password", ex.getErrorHandler());
            return Optional.empty();
        }
    }

    private Optional<User> findUser(final Email email, final Notification notification) {
        try {
            final var user = userGateway.findByEmail(email);
            if (user.isPresent()) {
                final var errorHandler = ErrorHandler.create()
                        .append(Error.with("email already used"));
                notification.append("email", errorHandler);
            }
            return user;
        } catch (final Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }
    }

    private Optional<User> createUser(final Email email, final Password password, final Notification notification) {
        try {
            return Optional.of(User.newUser(email, password));
        } catch (final ValidationException ex) {
            notification.append("user", ex.getErrorHandler());
            return Optional.empty();
        }
    }

    private User saveUser(final User user) {
        try {
            return userGateway.save(user);
        } catch (final Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }
    }

    private CreateUserOutput createOutput(final User user) {
        return CreateUserOutput.with(user.getId().getValue());
    }
}
