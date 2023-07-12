package org.auth.api.application.user.delete;

import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.exceptions.notification.IdentifierException;
import org.auth.api.domain.exceptions.notification.NotFoundException;
import org.auth.api.domain.exceptions.notification.NotificationException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.validation.Notification;
import org.auth.api.domain.valueobjects.Identifier;

import java.util.Optional;

public class DefaultDeleteUser extends DeleteUser {
    public DefaultDeleteUser(final UserGateway userGateway) {
        super(userGateway);
    }

    @Override
    public Void execute(final DeleteUserInput input) {
        final var id = createId(input.id());
        verifyUser(id);
        deleteUser(id);
        return null;
    }

    private Identifier createId(final String id) {
        try {
            return Identifier.with(id);
        } catch (final ValidationException ex) {
            final var notification = Notification.create()
                    .append("id", ex.getErrorHandler());
            throw IdentifierException.with(notification);
        }
    }

    private void verifyUser(final Identifier id) {
        Optional<User> user;
        try {
            user = userGateway.findById(id);
        } catch (final Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }
        if (user.isEmpty())
            throw NotFoundException.with(User.class, id);
    }

    private void deleteUser(final Identifier id) {
        try {
            userGateway.deleteById(id);
        } catch (final Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }
    }
}
