package org.auth.api.application.user;

import org.auth.api.application.user.find.DefaultFindUser;
import org.auth.api.application.user.find.FindUserInput;
import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.notification.NotFoundException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
public class FindUserTest {
    @Mock
    private UserGateway gateway;
    @InjectMocks
    private DefaultFindUser useCase;

    @BeforeEach
    public void cleanUp() {
        reset(gateway);
    }

    @Test
    public void givenAValidUserId_whenCallsExecute_thenReturnsTheUserInfo() {
        // given
        final var expectedUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));

        when(gateway.findById(expectedUser.getId()))
                .thenReturn(Optional.of(expectedUser));

        // when
        final var actualOutput = useCase.execute(FindUserInput.with(expectedUser.getId().getValue()));

        // then
        assertEquals(expectedUser.getId().getValue(), actualOutput.id());
        assertEquals(expectedUser.getEmail().getAddress(), actualOutput.email());

        verify(gateway, times(1)).findById(any());
    }

    @Test
    public void givenANullUserId_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final String expectedId = null;

        // when
        final var actualException = assertThrows(NotificationException.class,
                () -> useCase.execute(FindUserInput.with(expectedId)));

        final var errors = actualException.getNotification().getNotifications();

        // then
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("id"));
        assertEquals(1, errors.get("id").size());
        assertEquals("id must not be null", errors.get("id").get(0));
    }

    @Test
    public void givenAEmptyUserId_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var expectedId = "";

        // when
        final var actualException = assertThrows(NotificationException.class,
                () -> useCase.execute(FindUserInput.with(expectedId)));

        final var errors = actualException.getNotification().getNotifications();

        // then
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("id"));
        assertEquals(1, errors.get("id").size());
        assertEquals("id must not be empty", errors.get("id").get(0));
    }

    @Test
    public void givenAInvalidUserId_whenCallsExecute_thenThrowsANotificationException() {
        // given
        final var expectedId = "23sdc90lja";

        // when
        final var actualException = assertThrows(NotificationException.class,
                () -> useCase.execute(FindUserInput.with(expectedId)));

        final var errors = actualException.getNotification().getNotifications();

        // then
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("id"));
        assertEquals(1, errors.get("id").size());
        assertEquals("id is invalid", errors.get("id").get(0));
    }

    @Test
    public void givenANonExistentUserId_whenCallsExecute_thenThrowsANotFoundException() {
        // given
        final var expectedUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));
        final var expectedExceptionMessage = "user with id %s was not found".formatted(expectedUser.getId().getValue());

        when(gateway.findById(expectedUser.getId()))
                .thenReturn(Optional.empty());

        // when
        final var actualException = assertThrows(NotFoundException.class,
                () -> useCase.execute(FindUserInput.with(expectedUser.getId().getValue())));

        final var errors = actualException.getNotification().getNotifications();

        // then
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("id"));

        assertEquals(1, errors.get("id").size());
        assertEquals(expectedExceptionMessage, errors.get("id").get(0));

        verify(gateway, times(1)).findById(any());
    }

    @Test
    public void givenAValidUserId_whenCallsExecuteAndGatewayThrowsAnException_thenThrowsAInternalErrorException() {
        // given
        final var expectedUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));
        final var expectedExceptionMessage = "user gateway error";

        doThrow(GatewayException.with(expectedExceptionMessage, null))
                .when(gateway).findById(expectedUser.getId());

        // when
        final var actualException = assertThrows(GatewayException.class,
                () -> useCase.execute(FindUserInput.with(expectedUser.getId().getValue())));

        // then
        assertEquals(expectedExceptionMessage, actualException.getMessage());
        verify(gateway, times(1)).findById(any());
    }
}
