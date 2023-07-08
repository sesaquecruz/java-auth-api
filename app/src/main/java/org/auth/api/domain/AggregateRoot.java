package org.auth.api.domain;

import java.util.UUID;

public abstract class AggregateRoot extends Entity {
    protected AggregateRoot(final UUID id) {
        super(id);
    }
}
