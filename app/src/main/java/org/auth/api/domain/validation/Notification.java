package org.auth.api.domain.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification {
    private final Map<String, List<ValidationError>> errors;

    private Notification(Map<String, List<ValidationError>> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return new Notification(new HashMap<>());
    }

    public Notification append(final String item, final ValidationError error) {
        if (errors.containsKey(item))
            errors.get(item).add(error);
        else
            errors.put(item, new ArrayList<>(List.of(error)));
        return this;
    }

    public Notification append(final String item, final List<ValidationError> errors) {
        if (this.errors.containsKey(item))
            this.errors.get(item).addAll(errors);
        else
            this.errors.put(item, errors);
        return this;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public Map<String, List<ValidationError>> getErrors() {
        return errors;
    }
}
