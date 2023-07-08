package org.auth.api.domain.user;

import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Password;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unitTest")
public class UserTest {
    @Test
    public void givenValidData_whenCallsNewUser_thenCreatesAnUser() {
        // given
        final var expectedEmail = Email.newEmail("test@mail.com");
        final var expectedPassword = Password.newPassword("test123");

        // when
        final var actualUser = User.newUser(expectedEmail, expectedPassword);

        // then
        assertNotNull(actualUser.getId());
        assertEquals(expectedEmail, actualUser.getEmail());
        assertEquals(expectedPassword, actualUser.getPassword());
        assertNotNull(actualUser.getCreatedAt());
        assertNotNull(actualUser.getUpdatedAt());
        assertEquals(actualUser.getCreatedAt(), actualUser.getUpdatedAt());
    }

    @Test
    public void givenNullData_whenCallsNewUser_thenThrowsAValidationException() {
        // given
        final Email expectedEmail = null;
        final Password expectedPassword = null;

        // when
        final var actualException =
                assertThrows(ValidationException.class, () -> User.newUser(expectedEmail, expectedPassword));

        // then
        assertEquals(2, actualException.getErrors().size());
        assertEquals("email must not be null", actualException.getErrors().get(0).message());
        assertEquals("password must not be null", actualException.getErrors().get(1).message());
    }

    @Test
    public void givenValidData_whenCallsWith_thenCreatesAnUser() {
        // given
        final var expectedId = UUID.randomUUID();
        final var expectedEmail = Email.newEmail("test@mail.com");
        final var expectedPassword = Password.newPassword("test123");
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        // when
        final var actualUser = User.with(
                expectedId,
                expectedEmail,
                expectedPassword,
                expectedCreatedAt,
                expectedUpdatedAt
        );

        // then
        assertEquals(expectedId, actualUser.getId());
        assertEquals(expectedEmail, actualUser.getEmail());
        assertEquals(expectedPassword, actualUser.getPassword());
        assertEquals(expectedCreatedAt, actualUser.getCreatedAt());
        assertEquals(expectedUpdatedAt, actualUser.getUpdatedAt());
    }

    @Test
    public void givenNullData_whenCallsWith_thenThrowsAValidationException() {
        // given
        final UUID expectedId = null;
        final Email expectedEmail = null;
        final Password expectedPassword = null;
        final Instant expectedCreatedAt = null;
        final Instant expectedUpdatedAt = null;

        // when
        final var actualException = assertThrows(ValidationException.class, () -> User.with(
                expectedId,
                expectedEmail,
                expectedPassword,
                expectedCreatedAt,
                expectedUpdatedAt
        ));

        // then
        assertEquals(5, actualException.getErrors().size());
        assertEquals("id must not be null", actualException.getErrors().get(0).message());
        assertEquals("email must not be null", actualException.getErrors().get(1).message());
        assertEquals("password must not be null", actualException.getErrors().get(2).message());
        assertEquals("created at must not be null", actualException.getErrors().get(3).message());
        assertEquals("updated at must not be null", actualException.getErrors().get(4).message());
    }

    @Test
    public void givenInvalidAuditData_whenCallsWith_thenThrowsAValidationException() {
        // given
        final var expectedId = UUID.randomUUID();
        final var expectedEmail = Email.newEmail("test@mail.com");
        final var expectedPassword = Password.newPassword("test123");
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = expectedCreatedAt.minusSeconds(1);

        // when
        final var actualException = assertThrows(ValidationException.class, () -> User.with(
                expectedId,
                expectedEmail,
                expectedPassword,
                expectedCreatedAt,
                expectedUpdatedAt
        ));

        // then
        assertEquals(1, actualException.getErrors().size());
        assertEquals("update at must not come before created at", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnUserAndANewEmail_whenCallsUpdateEmail_thenUpdatesUserEmail() {
        // given
        final var expectedUser = User.newUser(Email.newEmail("test@mail.com"), Password.newPassword("test12"));

        final var expectedId = expectedUser.getId();
        final var newEmail = Email.newEmail("newtest@mail.com");
        final var expectedPassword = expectedUser.getPassword();
        final var expectedCreatedAt = expectedUser.getCreatedAt();
        final var oldUpdatedAt = expectedUser.getUpdatedAt();

        // when
        expectedUser.updateEmail(newEmail);

        // then
        assertEquals(expectedId, expectedUser.getId());
        assertEquals(newEmail, expectedUser.getEmail());
        assertEquals(expectedPassword, expectedUser.getPassword());
        assertEquals(expectedCreatedAt, expectedUser.getCreatedAt());
        assertNotEquals(oldUpdatedAt, expectedUser.getUpdatedAt());
        assertTrue(expectedUser.getUpdatedAt().isAfter(expectedUser.getCreatedAt()));
    }

    @Test
    public void givenAnUserAndANewPassword_whenCallsUpdateEmail_thenUpdatesUserPassword() {
        // given
        final var expectedUser = User.newUser(Email.newEmail("test@mail.com"), Password.newPassword("test12"));

        final var expectedId = expectedUser.getId();
        final var expectedEmail = expectedUser.getEmail();
        final var newPassword = Password.newPassword("newtest12");
        final var expectedCreatedAt = expectedUser.getCreatedAt();
        final var oldUpdatedAt = expectedUser.getUpdatedAt();

        // when
        expectedUser.updatePassword(newPassword);

        // then
        assertEquals(expectedId, expectedUser.getId());
        assertEquals(expectedEmail, expectedUser.getEmail());
        assertEquals(newPassword, expectedUser.getPassword());
        assertEquals(expectedCreatedAt, expectedUser.getCreatedAt());
        assertNotEquals(oldUpdatedAt, expectedUser.getUpdatedAt());
        assertTrue(expectedUser.getUpdatedAt().isAfter(expectedUser.getCreatedAt()));
    }
}
