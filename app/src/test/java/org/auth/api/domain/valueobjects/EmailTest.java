package org.auth.api.domain.valueobjects;

import org.auth.api.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unitTest")
public class EmailTest {
    @Test
    public void givenAValidAddress_whenCallsNewEmail_thenCreatesAnEmail() {
        final var expectedAddress = "test@mail.com";
        final var actualEmail = Email.newEmail(expectedAddress);
        assertEquals(expectedAddress, actualEmail.getAddress());
    }

    @Test
    public void givenAValidAddressWithSpaces_whenCallsNewEmail_thenStripsAddressAndCreatesAnEmail() {
        final var expectedAddress = "test@mail.com";
        final var actualEmail = Email.newEmail("       test@mail.com             ");
        assertEquals(expectedAddress, actualEmail.getAddress());
    }

    @Test
    public void givenAInvalidAddress_whenCallsNewEmail_thenThrowsAValidationException() {
        final String expectedAddress = "test@mailcom";
        final var actualException = assertThrows(ValidationException.class, () -> Email.newEmail(expectedAddress));
        assertEquals(1, actualException.getErrors().size());
        assertEquals("email is invalid", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenANullAddress_whenCallsNewEmail_thenThrowsAValidationException() {
        final String expectedAddress = null;
        final var actualException = assertThrows(ValidationException.class, () -> Email.newEmail(expectedAddress));
        assertEquals(1, actualException.getErrors().size());
        assertEquals("email must not be null", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAEmptyAddress_whenCallsNewEmail_thenThrowsAValidationException() {
        final String expectedAddress = "   ";
        final var actualException = assertThrows(ValidationException.class, () -> Email.newEmail(expectedAddress));
        assertEquals(2, actualException.getErrors().size());
        assertEquals("email must not be empty", actualException.getErrors().get(0).message());
        assertEquals("email is invalid", actualException.getErrors().get(1).message());
    }

    @Test
    public void givenAnAddressGreaterThan100Characters_whenCallsNewEmail_thenThrowsAValidationException() {
        final String expectedAddress = "hjs98231e123123iuo09132njqweqwuyuiwqeyuhasewuiio123dduiqwe12133uay128@ddsfsdfsderrtretffgioer.dfuoier";
        assertEquals(101, expectedAddress.length());
        final var actualException = assertThrows(ValidationException.class, () -> Email.newEmail(expectedAddress));
        assertEquals(1, actualException.getErrors().size());
        assertEquals("email must not have more than 100 characters", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAValue_whenCallsWith_thenCreatesAnEmail() {
        final var expectedAddress = "test@mail.com";
        final var actualEmail = Email.with(expectedAddress);
        assertEquals(expectedAddress, actualEmail.getAddress());
    }
}
