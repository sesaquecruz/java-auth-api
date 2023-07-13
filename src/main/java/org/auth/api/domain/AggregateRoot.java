package org.auth.api.domain;

import org.auth.api.domain.valueobjects.Identifier;

public abstract class AggregateRoot extends Entity {
    protected AggregateRoot(final Identifier id) {
        super(id);
    }
}
