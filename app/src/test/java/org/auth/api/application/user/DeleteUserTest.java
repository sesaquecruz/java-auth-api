package org.auth.api.application.user;

import org.auth.api.application.user.delete.DefaultDeleteUser;
import org.auth.api.application.user.delete.DeleteUserInput;
import org.auth.api.domain.exceptions.notification.IdentifierException;
import org.auth.api.domain.exceptions.notification.NotFoundException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.utils.IDUtils;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Identifier;
import org.auth.api.domain.valueobjects.Password;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
public class DeleteUserTest {
    @Mock
    private UserGateway gateway;
    @InjectMocks
    private DefaultDeleteUser useCase;

    @BeforeEach
    public void cleanUp() {
        reset(gateway);
    }

    @Test
    public void givenAValidAndExistentUserId_whenCallsExecute_thenDeletesTheUser() {
        // given
        final var expectedUser = User.newUser(Email.with("test@mail.com"), Password.withRawValue("test123"));
        final var expectedId = expectedUser.getId().getValue();

        when(gateway.findById(any()))
                .thenReturn(Optional.of(expectedUser));

        doNothing()
                .when(gateway).deleteById(any());

        // when
        useCase.execute(DeleteUserInput.with(expectedId));

        // then
        verify(gateway, times(1)).findById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));

        verify(gateway, times(1)).deleteById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));
    }

    @Test
    public void givenAValidAndNonExistentUserId_whenCallsExecute_thenThrowsANotFoundException() {
        // given
        final var expectedId = IDUtils.newUUID();
        final var expectedError = "%s with id %s was not found".formatted(User.class.getSimpleName(), expectedId)
                .toLowerCase();

        when(gateway.findById(any()))
                .thenReturn(Optional.empty());

        // when
        final var actualException = assertThrows(NotFoundException.class, () ->
                useCase.execute(DeleteUserInput.with(expectedId))
        );

        final var actualErrors = actualException.getNotification().getNotifications();

        // then
        assertEquals(1, actualErrors.size());
        assertEquals(1, actualErrors.get("id").size());
        assertEquals(expectedError, actualErrors.get("id").get(0));

        verify(gateway, times(1)).findById(argThat(id ->
                Objects.equals(expectedId, id.getValue())
        ));
    }

    @Test
    public void givenANonValidUserId_whenCallsExecute_thenThrowsAnIdentifierException() {
        // given
        final var expectedId = "09JHf12iouqwe";
        final var expectedError = "id is invalid";

        // when
        final var actualException = assertThrows(IdentifierException.class, () ->
                useCase.execute(DeleteUserInput.with(expectedId))
        );

        final var actualErrors = actualException.getNotification().getNotifications();

        // then
        assertEquals(1, actualErrors.size());
        assertEquals(1, actualErrors.get("id").size());
        assertEquals(expectedError, actualErrors.get("id").get(0));

        verify(gateway, times(0)).findById(any());
    }
}
