package org.auth.api.infrastructure.api;

import org.auth.api.application.user.create.CreateUser;
import org.auth.api.application.user.create.CreateUserOutput;
import org.auth.api.application.user.delete.DeleteUser;
import org.auth.api.application.user.find.FindUser;
import org.auth.api.application.user.find.FindUserOutput;
import org.auth.api.application.user.update.UpdateUser;
import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.notification.NotificationException;
import org.auth.api.domain.validation.Error;
import org.auth.api.domain.validation.ErrorHandler;
import org.auth.api.domain.validation.Notification;
import org.auth.api.infrastructure.ControllerTest;
import org.auth.api.infrastructure.config.UserDetailsConfig;
import org.auth.api.infrastructure.config.json.Json;
import org.auth.api.infrastructure.services.security.AuthTokenService;
import org.auth.api.infrastructure.user.models.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.UUID;

import static org.auth.api.infrastructure.config.UserDetailsConfig.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = UserApi.class)
public class UserApiIT {
    @Autowired
    private AuthTokenService authTokenService;
    @MockBean
    private CreateUser createUserUC;
    @MockBean
    private FindUser findUserUC;
    @MockBean
    private UpdateUser updateUserUC;
    @MockBean
    private DeleteUser deleteUserUC;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void cleanUp() {
        reset(createUserUC);
        reset(findUserUC);
        reset(updateUserUC);
    }

    private String getAuthToken() throws Exception {
        return mvc.perform(post("/users/login")
                        .with(httpBasic(USER_EMAIL, USER_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");
    }

    @Test
    public void givenValidData_whenAccessesCreateUser_thenCreatesAnUser() throws Exception {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";
        final var expectedId = UUID.randomUUID().toString();

        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

        when(createUserUC.execute(any()))
                .thenReturn(CreateUserOutput.with(expectedId));

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isCreated());

        verify(createUserUC, times(1)).execute(argThat(input ->
            Objects.equals(expectedEmail, input.email()) &&
            Objects.equals(expectedPassword, input.password())
        ));
    }

    @Test
    public void givenInvalidData_whenAccessesCreateUser_thenReturnsUnprocessableEntity() throws Exception {
        // given
        final var expectedEmail = "";
        final var expectedPassword = "test1";

        final var emailErrors = ErrorHandler.create()
                .append(Error.with("email must not be empty"))
                .append(Error.with("email is invalid"));

        final var passwordErrors = ErrorHandler.create()
                .append(Error.with("password must have more than 5 characters"));

        final var notification = Notification.create()
                .append("email", emailErrors)
                .append("password", passwordErrors);

        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

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
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.email[0]", equalTo("email must not be empty")))
                .andExpect(jsonPath("$.password[0]", equalTo("password must have more than 5 characters")));

        verify(createUserUC, times(1)).execute(argThat(input ->
                Objects.equals(expectedEmail, input.email()) &&
                        Objects.equals(expectedPassword, input.password())
        ));
    }

    @Test
    public void givenValidData_whenAccessesCreateUserAndOccursAGatewayError_thenReturnsInternalServerError() throws Exception {
        // given
        final var expectedEmail = "test@mail.com";
        final var expectedPassword = "test12";

        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

        doThrow(GatewayException.with("an error message", null))
                .when(createUserUC).execute(any());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("an error message"));

        verify(createUserUC, times(1)).execute(argThat(input ->
                Objects.equals(expectedEmail, input.email()) &&
                        Objects.equals(expectedPassword, input.password())
        ));
    }

    @Test
    public void givenValidCredentials_whenAccessesLoginUser_thenReturnsAnAuthorizationToken() throws Exception {
        // given
        final var email = USER_EMAIL;
        final var password = USER_PASSWORD;

        // when
        final var request = post("/users/login")
                .with(httpBasic(email, password));

        // then
        final var response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final var token = response.getHeader("Authorization");
        assertNotNull(token);

        final var sub = authTokenService.getSub(token);
        assertEquals(UserDetailsConfig.USER_ID, sub);
    }

    @Test
    public void givenInvalidCredentials_whenAccessesLoginUser_thenReturnsUnauthorized() throws Exception {
        // given
        final var email = USER_EMAIL;
        final var password = USER_PASSWORD + "1";

        // when
        final var request = post("/users/login")
                .with(httpBasic(email, password));

        // then
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnAuthenticatedUser_whenAccessesFindUser_thenReturnsHisInfo() throws Exception {
        // given
        final var token = getAuthToken();

        when(findUserUC.execute(any()))
                .thenReturn(FindUserOutput.with(USER_ID, USER_EMAIL));

        // when
        final var request = get("/users")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(USER_ID)))
                .andExpect(jsonPath("$.email", equalTo(USER_EMAIL)));

        verify(findUserUC, times(1)).execute(argThat(input ->
            Objects.equals(USER_ID, input.id())
        ));
    }

    @Test
    public void givenAnNonAuthenticatedUser_whenAccessesFindUser_thenReturnsUnauthorized() throws Exception {
        // given
        final var token = "";

        // when
        final var request = get("/users")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenValidCredentialsAndData_whenAccessesUpdateUser_thenUpdatesUserData() throws Exception {
        // given
        final var expectedEmail = "updatetest@mail.com";
        final var expectedPassword = "update123";

        final var authUserId = USER_ID;
        final var authToken = getAuthToken();
        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

        doNothing()
                .when(updateUserUC).execute(any());

        // when
        final var request = put("/users")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(request)
                .andExpect(status().isNoContent());

        verify(updateUserUC, times(1)).execute(argThat(input ->
                Objects.equals(authUserId, input.id()) &&
                Objects.equals(expectedEmail, input.email()) &&
                Objects.equals(expectedPassword, input.password())
        ));
    }

    @Test
    public void givenValidCredentialsAndInvalidData_whenAccessesUpdateUser_thenReturnsUnprocessableEntity() throws Exception {
        // given
        final var expectedEmail = "updatetest@mailcom";
        final var expectedPassword = "updat";

        final var emailErrors = ErrorHandler.create()
                .append(Error.with("email is invalid"));

        final var passwordErrors = ErrorHandler.create()
                .append(Error.with("password must have more than 5 characters"));

        final var notification = Notification.create()
                .append("email", emailErrors)
                .append("password", passwordErrors);

        final var authUserId = USER_ID;
        final var authToken = getAuthToken();
        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

        doThrow(NotificationException.with(notification))
                .when(updateUserUC).execute(any());

        // when
        final var request = put("/users")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.email[0]", equalTo("email is invalid")))
                .andExpect(jsonPath("$.password[0]", equalTo("password must have more than 5 characters")));

        verify(updateUserUC, times(1)).execute(argThat(input ->
                Objects.equals(authUserId, input.id()) &&
                        Objects.equals(expectedEmail, input.email()) &&
                        Objects.equals(expectedPassword, input.password())
        ));
    }

    @Test
    public void givenInvalidCredentialsAndValidData_whenAccessesUpdateUser_thenReturnsUnauthorized() throws Exception {
        // given
        final var expectedEmail = "updatetest@mail.com";
        final var expectedPassword = "update123";

        final var authToken = "";
        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

        // when
        final var request = put("/users")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenValidUserCredentials_whenAccessesDeleteUser_thenDeletesTheUser() throws Exception {
        // given
        final var authToken = getAuthToken();

        doNothing()
                .when(deleteUserUC).execute(any());

        // when
        final var request = delete("/users")
                .header("Authorization", authToken);

        // then
        mvc.perform(request)
                .andExpect(status().isNoContent());

        verify(deleteUserUC, times(1)).execute(argThat(input ->
                Objects.equals(USER_ID, input.id()))
        );
    }

    @Test
    public void givenInvalidUserCredentials_whenAccessesDeleteUser_thenReturnsUnauthorized() throws Exception {
        // given
        final var authToken = "";

        // when
        final var request = delete("/users")
                .header("Authorization", authToken);

        // then
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
