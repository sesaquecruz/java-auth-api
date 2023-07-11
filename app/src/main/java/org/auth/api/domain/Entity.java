package org.auth.api.domain;

import org.auth.api.domain.valueobjects.Identifier;

import java.util.Objects;

public abstract class Entity {
    private final Identifier id;

    protected Entity(final Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
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
