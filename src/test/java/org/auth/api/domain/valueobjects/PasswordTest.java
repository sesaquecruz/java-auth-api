package org.auth.api.domain.valueobjects;

import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.PasswordUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unitTest")
public class PasswordTest {
    @Test
    public void givenAValidPassword_whenCallsWithRawValue_thenCreatesAPassword() {
        final var expectedPassword = "test12";
        final var actualPassword = Password.withRawValue(expectedPassword);
        assertNotEquals(expectedPassword, actualPassword.getValue());
        assertTrue(PasswordUtils.isEncodedPassword(actualPassword.getValue()));
    }

    @Test
    public void givenAPasswordWithLessThan6Characters_whenCallsWithRawValue_thenThrowsAValidationException() {
        final String expectedPassword = "test1";
        final var actualException = assertThrows(ValidationException.class, () -> Password.withRawValue(expectedPassword));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("password must have more than 5 characters", errors.get(0).message());
    }

    @Test
    public void givenANullPassword_whenCallsWithRawValue_thenThrowsAValidationException() {
        final String expectedPassword = null;
        final var actualException = assertThrows(ValidationException.class, () -> Password.withRawValue(expectedPassword));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("password must not be null", errors.get(0).message());
    }

    @Test
    public void givenAEmptyPassword_whenCallsWithRawValue_thenThrowsAValidationException() {
        final String expectedPassword = "   ";
        final var actualException = assertThrows(ValidationException.class, () -> Password.withRawValue(expectedPassword));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("password must not be empty", errors.get(0).message());
    }

    @Test
    public void givenAPasswordLessThan6Characters_whenCallsWithRawValue_thenThrowsAValidationException() {
        final String expectedPassword = "12345";
        final var actualException = assertThrows(ValidationException.class, () -> Password.withRawValue(expectedPassword));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("password must have more than 5 characters", errors.get(0).message());
    }

    @Test
    public void givenAPasswordGreaterThan100Characters_whenCallsWithRawValue_thenThrowsAValidationException() {
        final String expectedPassword = "1HIU213123ui789#@asdlakjdkwqe123l123817sadu&892313yusdaiudy12371283asdhauey178231893123iU*(&89213huis";
        assertEquals(101, expectedPassword.length());
        final var actualException = assertThrows(ValidationException.class, () -> Password.withRawValue(expectedPassword));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("password must not have more than 100 characters", errors.get(0).message());
    }

    @Test
    public void givenAnEncodedPassword_whenCallsWithEncodedValue_thenCreatesAPassword() {
        final var expectedPassword = PasswordUtils.encodePassword("test12");
        final var actualPassword = Password.withEncodedValue(expectedPassword);
        assertEquals(expectedPassword, actualPassword.getValue());
    }

    @Test
    public void givenANonEncodedPassword_whenCallsWithEncodedValue_thenThrowsAValidationException() {
        final var expectedPassword = "test12";
        final var actualException = assertThrows(ValidationException.class, () -> Password.withEncodedValue(expectedPassword));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("password must be encoded", errors.get(0).message());
    }
}
