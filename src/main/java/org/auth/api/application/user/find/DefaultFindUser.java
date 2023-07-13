package org.auth.api.application.user.find;

import org.auth.api.domain.exceptions.*;
import org.auth.api.domain.exceptions.notification.IdentifierException;
import org.auth.api.domain.exceptions.notification.NotFoundException;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.validation.Notification;
import org.auth.api.domain.valueobjects.Identifier;

import java.util.Optional;

public class DefaultFindUser extends FindUser {
    public DefaultFindUser(final UserGateway userGateway) {
        super(userGateway);
    }

    @Override
    public FindUserOutput execute(final FindUserInput input) {
        final var notification = Notification.create();

        final var id = createIdentifier(input.id(), notification);
        if (id.isEmpty())
            throw IdentifierException.with(notification);

        final var user = findUser(id.get());
        if (user.isEmpty())
            throw NotFoundException.with(User.class, id.get());

        return createOutput(user.get());
    }

    private Optional<Identifier> createIdentifier(final String id, final Notification notification) {
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

    private FindUserOutput createOutput(final User user) {
        return FindUserOutput.with(user.getId().getValue(), user.getEmail().getAddress());
    }
}
