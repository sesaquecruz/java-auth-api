package org.auth.api.domain.valueobjects;

import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.utils.IDUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unitTest")
public class IdentifierTest {
    @Test
    public void givenAValidValue_whenCallsWith_thenCreatesAnIdentifier() {
        final var expectedValue = IDUtils.newUUID();
        final var actualIdentifier = Identifier.with(expectedValue);
        assertEquals(expectedValue, actualIdentifier.getValue());
    }

    @Test
    public void givenANullValidValue_whenCallsWith_thenCreatesAnIdentifier() {
        final String expectedValue = null;
        final var actualException = assertThrows(ValidationException.class, () -> Identifier.with(expectedValue));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("id must not be null", errors.get(0).message());
    }

    @Test
    public void givenAEmptyValidValue_whenCallsWith_thenCreatesAnIdentifier() {
        final String expectedValue = "";
        final var actualException = assertThrows(ValidationException.class, () -> Identifier.with(expectedValue));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("id must not be empty", errors.get(0).message());
    }

    @Test
    public void givenANonUUIDValue_whenCallsWith_thenCreatesAnIdentifier() {
        final String expectedValue = "okd1q123$13ud";
        final var actualException = assertThrows(ValidationException.class, () -> Identifier.with(expectedValue));
        final var errors = actualException.getErrorHandler().getErrors();
        assertEquals(1, errors.size());
        assertEquals("id is invalid", errors.get(0).message());
    }
}
