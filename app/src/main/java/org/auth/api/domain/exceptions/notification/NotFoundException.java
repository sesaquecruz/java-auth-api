package org.auth.api.domain.exceptions.notification;

import org.auth.api.domain.Entity;
import org.auth.api.domain.validation.Notification;
import org.auth.api.domain.validation.Error;
import org.auth.api.domain.validation.ErrorHandler;
import org.auth.api.domain.valueobjects.Identifier;

public class NotFoundException extends NotificationException {
    private NotFoundException(final Notification notification) {
        super(notification);
    }

    public static NotFoundException with(
            final Class<? extends Entity> entity,
            final Identifier id
    ) {
        final var message = "%s with id %s was not found"
                .formatted(entity.getSimpleName(), id.getValue())
                .toLowerCase();

        final var handler = ErrorHandler
                .create()
                .append(Error.with(message));

        final var notification = Notification
                .create()
                .append("id", handler);

        return new NotFoundException(notification);
    }
}
