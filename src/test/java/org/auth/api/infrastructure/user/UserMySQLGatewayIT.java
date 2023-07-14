package org.auth.api.infrastructure.user;

import org.auth.api.domain.user.User;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Password;
import org.auth.api.infrastructure.PersistenceTest;
import org.auth.api.infrastructure.user.persitence.UserJpaEntity;
import org.auth.api.infrastructure.user.persitence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PersistenceTest
public class UserMySQLGatewayIT {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserMySQLGateway gateway;

    @BeforeEach
    public void cleanUp() {
        repository.deleteAll();
    }

    @Test
    public void givenAnUser_whenCallsSave_thenSavesAndReturnsTheUser() {
        // given
        final var expectedUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test12"));
        assertEquals(0, repository.count());

        // when
        final var actualUser = gateway.save(expectedUser);
        final var savedEntity = repository.findById(expectedUser.getId().getValue());

        // then
        assertEquals(1, repository.count());
        assertTrue(savedEntity.isPresent());

        final var savedUser = savedEntity.get().toAggregate();

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        assertEquals(expectedUser.getCreatedAt(), actualUser.getCreatedAt());
        assertEquals(expectedUser.getUpdatedAt(), actualUser.getUpdatedAt());

        assertEquals(expectedUser.getId(), savedUser.getId());
        assertEquals(expectedUser.getEmail(), savedUser.getEmail());
        assertEquals(expectedUser.getPassword(), savedUser.getPassword());
        assertEquals(expectedUser.getCreatedAt(), savedUser.getCreatedAt());
        assertEquals(expectedUser.getUpdatedAt(), savedUser.getUpdatedAt());
    }

    @Test
    public void givenAnExistentUser_whenCallsFindById_thenReturnsTheUser() {
        // given
        final var expectedUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test12"));
        assertEquals(0, repository.count());

        repository.save(UserJpaEntity.from(expectedUser));
        assertEquals(1, repository.count());

        // when
        final var actualUser = gateway.findById(expectedUser.getId()).get();

        // then
        assertEquals(1, repository.count());

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        assertEquals(expectedUser.getCreatedAt(), actualUser.getCreatedAt());
        assertEquals(expectedUser.getUpdatedAt(), actualUser.getUpdatedAt());
    }

    @Test
    public void givenAnExistentUser_whenCallsFindByEmail_thenReturnsTheUser() {
        // given
        final var expectedUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test12"));
        assertEquals(0, repository.count());

        repository.save(UserJpaEntity.from(expectedUser));
        assertEquals(1, repository.count());

        // when
        final var actualUser = gateway.findByEmail(expectedUser.getEmail()).get();

        // then
        assertEquals(1, repository.count());

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        assertEquals(expectedUser.getCreatedAt(), actualUser.getCreatedAt());
        assertEquals(expectedUser.getUpdatedAt(), actualUser.getUpdatedAt());
    }
}
