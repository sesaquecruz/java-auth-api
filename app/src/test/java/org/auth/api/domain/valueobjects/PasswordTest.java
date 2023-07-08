package org.auth.api.domain.valueobjects;

import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.PasswordUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unitTest")
public class PasswordTest {
    @Test
    public void givenAValidPassword_whenCallsNewPassword_thenCreatesAPassword() {
        final var expectedPassword = "test12";
        final var actualPassword = Password.newPassword(expectedPassword);
        assertNotEquals(expectedPassword, actualPassword.getValue());
        assertTrue(PasswordUtils.isEncodedPassword(actualPassword.getValue()));
    }

    @Test
    public void givenAPasswordWithLessThan6Characters_whenCallsNewPassword_thenThrowsAValidationException() {
        final String expectedPassword = "test1";
        final var actualException = assertThrows(ValidationException.class, () -> Password.newPassword(expectedPassword));
        assertEquals(1, actualException.getErrors().size());
        assertEquals("password must have more than 5 characters", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenANullPassword_whenCallsNewPassword_thenThrowsAValidationException() {
        final String expectedPassword = null;
        final var actualException = assertThrows(ValidationException.class, () -> Password.newPassword(expectedPassword));
        assertEquals(1, actualException.getErrors().size());
        assertEquals("password must not be null", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAEmptyPassword_whenCallsNewPassword_thenThrowsAValidationException() {
        final String expectedPassword = "   ";
        final var actualException = assertThrows(ValidationException.class, () -> Password.newPassword(expectedPassword));
        assertEquals(2, actualException.getErrors().size());
        assertEquals("password must not be empty", actualException.getErrors().get(0).message());
        assertEquals("password must have more than 5 characters", actualException.getErrors().get(1).message());
    }

    @Test
    public void givenAPasswordGreaterThan100Characters_whenCallsNewPassword_thenThrowsAValidationException() {
        final String expectedPassword = "1HIU213123ui789#@asdlakjdkwqe123l123817sadu&892313yusdaiudy12371283asdhauey178231893123iU*(&89213huis";
        assertEquals(101, expectedPassword.length());
        final var actualException = assertThrows(ValidationException.class, () -> Password.newPassword(expectedPassword));
        assertEquals(1, actualException.getErrors().size());
        assertEquals("password must not have more than 100 characters", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAValue_whenCallsWith_thenCreatesAPassword() {
        final var expectedPassword = "test12";
        final var actualPassword = Password.with(expectedPassword);
        assertEquals(expectedPassword, actualPassword.getValue());
    }
}
