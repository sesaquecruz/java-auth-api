package org.auth.api.application.user;

import org.auth.api.application.user.update.DefaultUpdateUser;
import org.auth.api.application.user.update.UpdateUserInput;
import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.notification.NotFoundException;
import org.auth.api.domain.exceptions.notification.NotificationException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.utils.IDUtils;
import org.auth.api.domain.utils.PasswordUtils;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
public class UpdateUserTest {
    @Mock
    private UserGateway gateway;
    @InjectMocks
    private DefaultUpdateUser useCase;

    @BeforeEach
    public void cleanUp() {
        reset(gateway);
    }

    @Test
    public void givenValidData_whenCallsExecute_thenUpdatesUserData() {
        // given
        final var actualUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));

        final var expectedId = actualUser.getId().getValue();
        final var expectedEmail = "newtest@mail.com";
        final var expectedPassword = "newtest123";

        when(gateway.findById(any()))
                .thenReturn(Optional.of(actualUser));

        when(gateway.findByEmail(any()))
                .thenReturn(Optional.empty());

        when(gateway.save(any()))
                .thenAnswer(returnsFirstArg());

        // when
        useCase.execute(UpdateUserInput.with(expectedId, expectedEmail, expectedPassword));

        // then
        verify(gateway, times(1)).findById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));

        verify(gateway, times(1)).findByEmail(argThat(email ->
                Objects.equals(expectedEmail, email.getAddress())
        ));

        verify(gateway, times(1)).save(argThat(user ->
            Objects.equals(expectedEmail, user.getEmail().getAddress()) &&
            PasswordUtils.verifyPassword(expectedPassword, user.getPassword().getValue())
        ));
    }

    @Test
    public void givenInvalidData_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var actualUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));

        final var expectedId = actualUser.getId().getValue();
        final var expectedEmail = "test@mailcom";
        final var expectedPassword = "test1";

        final var expectedErrors = Map.of(
                "email", List.of("email is invalid"),
                "password", List.of("password must have more than 5 characters")
        );

        when(gateway.findById(any()))
                .thenReturn(Optional.of(actualUser));

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
                useCase.execute(UpdateUserInput.with(expectedId, expectedEmail, expectedPassword))
        );

        final var actualErrors = actualException.getNotification().getNotifications();

        // then
        assertEquals(expectedErrors.size(), actualErrors.size());

        assertEquals(expectedErrors.get("email").size(), actualErrors.get("email").size());
        assertEquals(expectedErrors.get("email").get(0), actualErrors.get("email").get(0));

        assertEquals(expectedErrors.get("password").size(), actualErrors.get("password").size());
        assertEquals(expectedErrors.get("password").get(0), actualErrors.get("password").get(0));

        verify(gateway, times(1)).findById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));
    }

    @Test
    public void givenAnInvalidId_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var expectedId = "in137(23jhsda";
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";

        final var expectedErrors = Map.of(
                "id", List.of("id is invalid")
        );

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
            useCase.execute(UpdateUserInput.with(expectedId, expectedEmail, expectedPassword))
        );

        final var actualErrors = actualException.getNotification().getNotifications();

        // then
        assertEquals(expectedErrors.size(), actualErrors.size());

        assertEquals(expectedErrors.get("id").size(), actualErrors.get("id").size());
        assertEquals(expectedErrors.get("id").get(0), actualErrors.get("id").get(0));
    }

    @Test
    public void givenAnAlreadyUsedEmail_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var actualUser1 = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));
        final var actualUser2 = User.newUser(Email.with("newtest@mail.com"), Password.withRawValue("test123"));

        final var expectedId = actualUser1.getId().getValue();
        final var expectedEmail = "newtest@mail.com";
        final var expectedPassword = "newtest123";

        when(gateway.findById(any()))
                .thenReturn(Optional.of(actualUser1));

        when(gateway.findByEmail(any()))
                .thenReturn(Optional.of(actualUser2));

        final var expectedErrors = Map.of(
                "email", List.of("email already used")
        );

        // when
        final var actualException = assertThrows(NotificationException.class, () ->
                useCase.execute(UpdateUserInput.with(expectedId, expectedEmail, expectedPassword))
        );

        final var actualErrors = actualException.getNotification().getNotifications();

        // then
        assertEquals(expectedErrors.size(), actualErrors.size());

        assertEquals(expectedErrors.get("email").size(), actualErrors.get("email").size());
        assertEquals(expectedErrors.get("email").get(0), actualErrors.get("email").get(0));

        verify(gateway, times(1)).findById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));

        verify(gateway, times(1)).findByEmail(argThat(email ->
                Objects.equals(expectedEmail, email.getAddress())
        ));
    }

    @Test
    public void givenANonExistentId_whenCallsExecute_thenThrowsANotFoundException() {
        // given
        final var expectedId = IDUtils.newUUID();
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test123";

        when(gateway.findById(any()))
                .thenReturn(Optional.empty());

        final var expectedErrors = Map.of(
                "id", List.of(
                        "%s with id %s was not found".formatted(User.class.getSimpleName(), expectedId).toLowerCase())
        );

        // when
        final var actualException = assertThrows(NotFoundException.class, () ->
                useCase.execute(UpdateUserInput.with(expectedId, expectedEmail, expectedPassword))
        );

        final var actualErrors = actualException.getNotification().getNotifications();

        // then
        assertEquals(expectedErrors.size(), actualErrors.size());

        assertEquals(expectedErrors.get("id").size(), actualErrors.get("id").size());
        assertEquals(expectedErrors.get("id").get(0), actualErrors.get("id").get(0));

        verify(gateway, times(1)).findById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));
    }

    @Test
    public void givenValidData_whenCallsExecuteAndGatewayThrowsAnException_thenThrowsAGatewayException() {
        // given
        final var actualUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));

        final var expectedId = actualUser.getId().getValue();
        final var expectedEmail = "newtest@mail.com";
        final var expectedPassword = "newtest123";

        doThrow(GatewayException.with(GatewayException.USER_GATEWAY_ERROR, null))
                .when(gateway).findById(any());

        // when
        final var actualException = assertThrows(GatewayException.class, () ->
                useCase.execute(UpdateUserInput.with(expectedId, expectedEmail, expectedPassword))
        );

        // then
        assertEquals(GatewayException.USER_GATEWAY_ERROR, actualException.getMessage());

        verify(gateway, times(1)).findById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));
    }
}
