package org.auth.api.domain.exceptions.notification;

import org.auth.api.domain.exceptions.DomainException;
import org.auth.api.domain.validation.Notification;

public class NotificationException extends DomainException {
    private final Notification notification;

    protected NotificationException(final Notification notification) {
        this.notification = notification;
    }

    public static NotificationException with(final Notification notification) {
        return new NotificationException(notification);
    }

    public Notification getNotification() {
        return notification;
    }
}
