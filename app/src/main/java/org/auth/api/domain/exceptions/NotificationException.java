package org.auth.api.domain.exceptions;

import org.auth.api.domain.validation.Notification;
import org.auth.api.domain.validation.ValidationError;

import java.util.List;
import java.util.Map;

public class NotificationException extends DomainException {
    private final Map<String, List<ValidationError>> errors;

    private NotificationException(Map<String, List<ValidationError>> errors) {
        this.errors = errors;
    }

    public static NotificationException with(final Notification notification) {
        return new NotificationException(notification.getErrors());
    }

    public Map<String, List<ValidationError>> getErrors() {
        return errors;
    }
}
