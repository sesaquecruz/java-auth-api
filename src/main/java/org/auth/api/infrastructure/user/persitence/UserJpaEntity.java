package org.auth.api.infrastructure.user.persitence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.auth.api.domain.user.User;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Identifier;
import org.auth.api.domain.valueobjects.Password;

import java.time.Instant;

@Entity(name = "user")
@Table(name = "users")
public class UserJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    private UserJpaEntity(
            final String id,
            final String email,
            final String password,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserJpaEntity() { }

    public static UserJpaEntity from(final User user) {
        return new UserJpaEntity(
                user.getId().getValue(),
                user.getEmail().getAddress(),
                user.getPassword().getValue(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public User toAggregate() {
        return User.with(
                Identifier.with(getId()),
                Email.with(getEmail()),
                Password.withEncodedValue(getPassword()),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
