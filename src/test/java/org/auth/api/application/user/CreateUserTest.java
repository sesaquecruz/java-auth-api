package org.auth.api.application.user;

import org.auth.api.application.user.create.CreateUserInput;
import org.auth.api.application.user.create.DefaultCreateUser;
import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.notification.NotificationException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
public class CreateUserTest {
    @Mock
    private UserGateway gateway;
    @InjectMocks
    private DefaultCreateUser useCase;

    @BeforeEach
    public void cleanUp() {
        reset(gateway);
    }

    @Test
    public void givenValidData_whenCallsExecute_thenCreatesAnUser() {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";

        when(gateway.findByEmail(any()))
                .thenReturn(Optional.empty());

        when(gateway.save(any()))
                .thenAnswer(returnsFirstArg());

        // when
        final var actualOutput = useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword));

        // then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(gateway, times(1)).findByEmail(argThat(email ->
            Objects.equals(expectedEmail, email.getAddress())
        ));

        verify(gateway, times(1)).save(argThat(user ->
            Objects.nonNull(user) &&
            Objects.equals(actualOutput.id(), user.getId().getValue())
        ));
    }

    @Test
    public void givenNullData_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final String expectedEmail = null;
        final String expectedPassword = null;

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
                useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword))
        );

        // then
        final var errors = actualException.getNotification().getNotifications();
        final var emailErrors = errors.get("email");
        final var passwordErrors = errors.get("password");

        assertEquals(2, errors.size());
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("password"));

        assertEquals(1, emailErrors.size());
        assertEquals("email must not be null", emailErrors.get(0));

        assertEquals(1, passwordErrors.size());
        assertEquals("password must not be null", passwordErrors.get(0));
    }

    @Test
    public void givenEmptyData_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var expectedEmail = "  ";
        final var expectedPassword = "";

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
                useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword))
        );

        // then
        final var errors = actualException.getNotification().getNotifications();
        final var emailErrors = errors.get("email");
        final var passwordErrors = errors.get("password");

        assertEquals(2, errors.size());
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("password"));

        assertEquals(1, emailErrors.size());
        assertEquals("email must not be empty", emailErrors.get(0));

        assertEquals(1, passwordErrors.size());
        assertEquals("password must not be empty", passwordErrors.get(0));
    }

    @Test
    public void givenAInvalidEmail_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var expectedEmail = "test@mailcom";
        final var expectedPassword = "test12";

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
                useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword))
        );

        // then
        final var errors = actualException.getNotification().getNotifications();
        final var emailErrors = errors.get("email");

        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("email"));

        assertEquals(1, emailErrors.size());
        assertEquals("email is invalid", emailErrors.get(0));
    }

    @Test
    public void givenAPasswordWithLessThan6Characters_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test1";

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
                useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword))
        );

        // then
        final var errors = actualException.getNotification().getNotifications();
        final var emailErrors = errors.get("password");

        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("password"));

        assertEquals(1, emailErrors.size());
        assertEquals("password must have more than 5 characters", emailErrors.get(0));
    }

    @Test
    public void givenAnEmailAlreadyUsed_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";
        final var expectedUser = User.newUser(Email.with(expectedEmail), Password.withRawValue(expectedEmail));

        when(gateway.findByEmail(any()))
                .thenReturn(Optional.of(expectedUser));

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
                useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword))
        );

        // then
        final var errors = actualException.getNotification().getNotifications();
        final var emailErrors = errors.get("email");

        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("email"));

        assertEquals(1, emailErrors.size());
        assertEquals("email already used", emailErrors.get(0));

        verify(gateway, times(1)).findByEmail(argThat(email ->
            Objects.equals(expectedEmail, email.getAddress())
        ));
    }

    @Test
    public void givenAValidData_whenCallsExecuteAndGatewayThrowsAnExceptionTryingFindUser_thenThrowsAnInternalErrorException() {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";
        final var expectedExceptionMessage = "user gateway error";

        doThrow(GatewayException.with(expectedExceptionMessage, null))
                .when(gateway).findByEmail(any());

        // when
        final var actualException = assertThrows(GatewayException.class, () ->
                useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword))
        );

        // then
        assertEquals(expectedExceptionMessage, actualException.getMessage());

        verify(gateway, times(1)).findByEmail(any());
        verify(gateway, times(0)).save(any());
    }

    @Test
    public void givenAValidData_whenCallsExecuteAndGatewayThrowsAnExceptionTryingSaveUser_thenThrowsAnInternalErrorException() {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";
        final var expectedExceptionMessage = "user gateway error";

        when(gateway.findByEmail(any()))
                .thenReturn(Optional.empty());

        doThrow(GatewayException.with(expectedExceptionMessage, null))
                .when(gateway).save(any());

        // when
        final var actualException = assertThrows(GatewayException.class, () ->
                useCase.execute(CreateUserInput.with(expectedEmail, expectedPassword))
        );

        // then
        assertEquals(expectedExceptionMessage, actualException.getMessage());

        verify(gateway, times(1)).findByEmail(any());
        verify(gateway, times(1)).save(any());
    }
}
