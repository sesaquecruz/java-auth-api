package org.auth.api.infrastructure.api;

import org.auth.api.application.user.CreateUser;
import org.auth.api.application.user.CreateUserOutput;
import org.auth.api.domain.exceptions.InternalErrorException;
import org.auth.api.domain.exceptions.NotificationException;
import org.auth.api.domain.validation.Notification;
import org.auth.api.domain.validation.ValidationError;
import org.auth.api.domain.validation.ValidationHandler;
import org.auth.api.infrastructure.ControllerTest;
import org.auth.api.infrastructure.config.json.Json;
import org.auth.api.infrastructure.user.models.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = UserApi.class)
public class UserApiIT {
    @MockBean
    private CreateUser createUserUC;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void cleanUp() {
        reset(createUserUC);
    }

    @Test
    public void givenValidData_whenAccessesCreateUser_thenCreatesAnUser() throws Exception {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";
        final var expectedId = UUID.randomUUID().toString();

        final var requestContent = Json.marshal(new CreateUserRequest(expectedEmail, expectedPassword));

        when(createUserUC.execute(any()))
                .thenReturn(CreateUserOutput.with(expectedId));

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/%s".formatted(expectedId)));

        verify(createUserUC, times(1)).execute(argThat(input ->
            Objects.equals(expectedEmail, input.email()) &&
            Objects.equals(expectedPassword, input.password())
        ));
    }

    @Test
    public void givenInvalidData_whenAccessesCreateUser_thenReturnsAnUnprocessableEntity() throws Exception {
        // given
        final var expectedEmail = "";
        final var expectedPassword = "test1";

        final var emailErrors = ValidationHandler.create()
                .append(ValidationError.with("email must not be empty"))
                .append(ValidationError.with("email is invalid"));

        final var passwordErrors = ValidationHandler.create()
                .append(ValidationError.with("password must have more than 5 characters"));

        final var notification = Notification.create()
                .append("email", emailErrors.getErrors())
                .append("password", passwordErrors.getErrors());

        final var requestContent = Json.marshal(new CreateUserRequest(expectedEmail, expectedPassword));

        doThrow(NotificationException.with(notification))
                .when(createUserUC).execute(any());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.email[0].message", equalTo("email must not be empty")))
                .andExpect(jsonPath("$.password[0].message", equalTo("password must have more than 5 characters")));

        verify(createUserUC, times(1)).execute(argThat(input ->
                Objects.equals(expectedEmail, input.email()) &&
                        Objects.equals(expectedPassword, input.password())
        ));
    }

    @Test
    public void givenAData_whenAccessesCreateUserAndOccursAnError_thenReturnsAnInternalServerError() throws Exception {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";

        final var requestContent = Json.marshal(new CreateUserRequest(expectedEmail, expectedPassword));

        doThrow(InternalErrorException.with("an error message", null))
                .when(createUserUC).execute(any());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("an error message"));

        verify(createUserUC, times(1)).execute(argThat(input ->
                Objects.equals(expectedEmail, input.email()) &&
                        Objects.equals(expectedPassword, input.password())
        ));
    }
}
