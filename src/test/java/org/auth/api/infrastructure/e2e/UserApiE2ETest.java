package org.auth.api.infrastructure.e2e;

import org.auth.api.domain.user.User;
import org.auth.api.domain.utils.PasswordUtils;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Password;
import org.auth.api.infrastructure.E2ETest;
import org.auth.api.infrastructure.config.json.Json;
import org.auth.api.infrastructure.services.security.AuthTokenService;
import org.auth.api.infrastructure.user.models.UserRequest;
import org.auth.api.infrastructure.user.persitence.UserJpaEntity;
import org.auth.api.infrastructure.user.persitence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@E2ETest
@Testcontainers
public class UserApiE2ETest {
    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mvc;

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0.32")
                    .withDatabaseName("auth_api")
                    .withUsername("root")
                    .withPassword("root123");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        final var mappedPort = MYSQL_CONTAINER.getMappedPort(3306);
        registry.add("mysql.port", () -> mappedPort);
    }

    @BeforeEach
    public void verifyContainerAndClearRepository() {
        assertTrue(MYSQL_CONTAINER.isRunning());
        userRepository.deleteAll();
    }

    private String getAuthToken(final String email, final String password) throws Exception {
        final var token = mvc.perform(post("/users/login")
                        .with(httpBasic(email, password))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        return requireNonNull(token);
    }

    @Test
    public void givenValidData_whenAccessesCreateUser_thenCreatesAnUser() throws Exception {
        // given
        final var email = "test@mail.com";
        final var password = "test12";
        final var requestContent = Json.marshal(new UserRequest(email, password));
        assertEquals(0, userRepository.count());

        // when
        final var request = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(request)
                .andExpect(status().isCreated());

        assertEquals(1, userRepository.count());
        assertTrue(userRepository.findByEmail(email).isPresent());
    }

    @Test
    public void givenNullData_whenAccessesCreateUser_thenReturnsUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new UserRequest(null, null));
        assertEquals(0, userRepository.count());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.email[0]", equalTo("email must not be null")))
                .andExpect(jsonPath("$.password[0]", equalTo("password must not be null")));

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenEmptyData_whenAccessesCreateUser_thenReturnsUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new UserRequest("", ""));
        assertEquals(0, userRepository.count());

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
                .andExpect(jsonPath("$.password[0]", equalTo("password must not be empty")));

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenAnInvalidEmail_whenAccessesCreateUser_thenReturnsUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new UserRequest("test@mailcom", "test12"));
        assertEquals(0, userRepository.count());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.email[0]", equalTo("email is invalid")));

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenAPasswordLessThan5Characters_whenAccessesCreateUser_thenReturnsUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new UserRequest("test@mail.com", "test1"));
        assertEquals(0, userRepository.count());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.password[0]", equalTo("password must have more than 5 characters")));

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenValidCredentials_whenAccessesLoginUser_thenReturnsAnAuthorizationToken() throws Exception {
        // given
        final var email = "test@mail.com";
        final var password = "test123";
        final var user = User.newUser(Email.with(email), Password.withRawValue(password));
        final var id = user.getId().getValue();

        userRepository.save(UserJpaEntity.from(user));
        assertEquals(1, userRepository.count());

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
        assertEquals(id, sub);
    }

    @Test
    public void givenInvalidCredentials_whenAccessesLoginUser_thenReturnsUnauthorized() throws Exception {
        // given
        final var email = "test@mail.com";
        final var password = "test123";

        // when
        final var request = post("/users")
                .with(httpBasic(email, password));

        // then
        final var response = mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnAuthenticatedUser_whenAccessesFindUser_thenReturnsHisInfo() throws Exception {
        // given
        final var email = "test@mail.com";
        final var password = "test123";
        final var user = User.newUser(Email.with(email), Password.withRawValue(password));
        final var id = user.getId().getValue();

        userRepository.save(UserJpaEntity.from(user));
        assertEquals(1, userRepository.count());

        final var token = getAuthToken(email, password);

        // when
        final var request = get("/users")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(id)))
                .andExpect(jsonPath("$.email", equalTo(email)));
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
        final var email = "update@mail.com";
        final var password = "update123";
        final var user = User.newUser(Email.with(email), Password.withRawValue(password));
        final var id = user.getId().getValue();

        userRepository.save(UserJpaEntity.from(user));
        assertEquals(1, userRepository.count());

        final var authToken = getAuthToken(email, password);

        final var expectedEmail = "newupdate@mail.com";
        final var expectedPassword = "newupdated123";
        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

        // when
        final var request = put("/users")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);
        // then
        mvc.perform(request)
                .andExpect(status().isNoContent());

        assertEquals(1, userRepository.count());
        final var actualUser = userRepository.findById(id).get().toAggregate();

        assertEquals(actualUser.getEmail().getAddress(), expectedEmail);
        assertTrue(PasswordUtils.verifyPassword(expectedPassword, actualUser.getPassword().getValue()));
    }

    @Test
    public void givenValidCredentialsAndInvalidData_whenAccessesUpdateUser_thenReturnsUnprocessableEntity() throws Exception {
        // given
        final var email = "update@mail.com";
        final var password = "update123";
        final var user = User.newUser(Email.with(email), Password.withRawValue(password));
        final var id = user.getId().getValue();

        userRepository.save(UserJpaEntity.from(user));
        assertEquals(1, userRepository.count());

        final var authToken = getAuthToken(email, password);

        final var expectedEmail = "newupdate@mailcom";
        final var expectedPassword = "newup";
        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

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

        assertEquals(1, userRepository.count());
        final var actualUser = userRepository.findById(id).get().toAggregate();

        assertNotEquals(actualUser.getEmail().getAddress(), expectedEmail);
        assertFalse(PasswordUtils.verifyPassword(expectedPassword, actualUser.getPassword().getValue()));

        assertEquals(actualUser.getEmail().getAddress(), email);
        assertTrue(PasswordUtils.verifyPassword(password, actualUser.getPassword().getValue()));
    }

    @Test
    public void givenInvalidCredentialsAndValidData_whenAccessesUpdateUser_thenReturnsUnauthorized() throws Exception {
        // given
        final var email = "update@mail.com";
        final var password = "update123";
        final var user = User.newUser(Email.with(email), Password.withRawValue(password));
        final var id = user.getId().getValue();

        userRepository.save(UserJpaEntity.from(user));
        assertEquals(1, userRepository.count());

        final var authToken = "";

        final var expectedEmail = "newupdate@mail.com";
        final var expectedPassword = "newupdated123";
        final var requestContent = Json.marshal(new UserRequest(expectedEmail, expectedPassword));

        // when
        final var request = put("/users")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);
        // then
        mvc.perform(request)
                .andExpect(status().isUnauthorized());

        assertEquals(1, userRepository.count());
        final var actualUser = userRepository.findById(id).get().toAggregate();

        assertNotEquals(actualUser.getEmail().getAddress(), expectedEmail);
        assertFalse(PasswordUtils.verifyPassword(expectedPassword, actualUser.getPassword().getValue()));

        assertEquals(actualUser.getEmail().getAddress(), email);
        assertTrue(PasswordUtils.verifyPassword(password, actualUser.getPassword().getValue()));
    }

    @Test
    public void givenValidUserCredentials_whenAccessesDeleteUser_thenDeletesTheUser() throws Exception {
        // given
        final var email = "test@mail.com";
        final var password = "test123";
        final var user = User.newUser(Email.with(email), Password.withRawValue(password));

        userRepository.save(UserJpaEntity.from(user));
        assertEquals(1, userRepository.count());

        final var authToken = getAuthToken(email, password);

        // when
        final var request = delete("/users")
                .header("Authorization", authToken);
        // then
        mvc.perform(request)
                .andExpect(status().isNoContent());

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenInvalidUserCredentials_whenAccessesDeleteUser_thenReturnsUnauthorized() throws Exception {
        // given
        final var email = "test@mail.com";
        final var password = "test123";
        final var user = User.newUser(Email.with(email), Password.withRawValue(password));
        final var id = user.getId().getValue();

        userRepository.save(UserJpaEntity.from(user));
        assertEquals(1, userRepository.count());

        final var authToken = "";

        // when
        final var request = delete("/users")
                .header("Authorization", authToken);
        // then
        mvc.perform(request)
                .andExpect(status().isUnauthorized());

        assertEquals(1, userRepository.count());
        assertTrue(userRepository.findById(id).isPresent());
    }
}
