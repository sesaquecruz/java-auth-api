package org.auth.api.domain.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Notification {
    private final Map<String, ErrorHandler> handlers;

    private Notification(final Map<String, ErrorHandler> handlers) {
        this.handlers = handlers;
    }

    public static Notification create() {
        return new Notification(new HashMap<>());
    }

    public Notification append(final String item, final ErrorHandler handler) {
        handlers.put(item, handler);
        return this;
    }

    public boolean hasNotification() {
        return !handlers.isEmpty();
    }

    public Map<String, List<String>> getNotifications() {
        return handlers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getErrors().stream()
                                .map(Error::message)
                                .toList()
                ));
    }
}
