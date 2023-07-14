package org.auth.api.domain.user;

import org.auth.api.domain.AggregateRoot;
import org.auth.api.domain.utils.TimeUtils;
import org.auth.api.domain.utils.IDUtils;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Identifier;
import org.auth.api.domain.valueobjects.Password;

import java.time.Instant;

public class User extends AggregateRoot {
    private Email email;
    private Password password;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(
            final Identifier id,
            final Email email,
            final Password password,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id);
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    public static User newUser(
            final Email email,
            final Password password
    ) {
        final var id = Identifier.with(IDUtils.newUUID());
        final var now = TimeUtils.now();
        return new User(id, email, password, now, now);
    }

    public static User with(
            final Identifier id,
            final Email email,
            final Password password,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new User(id, email, password, createdAt, updatedAt);
    }

    @Override
    protected void validate() {
        UserValidator.with(this).validate();
    }

    public User updateEmail(final Email email) {
        this.email = email;
        this.updatedAt = TimeUtils.now();
        validate();
        return this;
    }

    public User updatePassword(final Password password) {
        this.password = password;
        this.updatedAt = TimeUtils.now();
        validate();
        return this;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
