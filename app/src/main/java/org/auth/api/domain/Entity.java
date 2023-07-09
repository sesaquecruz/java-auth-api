package org.auth.api.domain;

import java.util.Objects;
import java.util.UUID;

public abstract class Entity {
    private final UUID id;

    protected Entity(final UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    protected abstract void validate();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Entity entity = (Entity) o;
        return Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
