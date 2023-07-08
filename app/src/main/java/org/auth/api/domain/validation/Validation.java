package org.auth.api.domain.validation;

import org.auth.api.domain.Entity;

public abstract class Validation<T extends Entity> {
    private final T entity;

    protected Validation(final T entity) {
        this.entity = entity;
    }

    protected abstract void validate();

    protected T getEntity() {
        return entity;
    }
}
