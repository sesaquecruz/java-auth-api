package org.auth.api.domain.exceptions.notification;

import org.auth.api.domain.validation.Notification;

public class IdentifierException extends NotificationException {
    private IdentifierException(final Notification notification) {
        super(notification);
    }

    public static IdentifierException with(final Notification notification) {
        return new IdentifierException(notification);
    }
}
