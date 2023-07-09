package org.auth.api.infrastructure.e2e;

import org.auth.api.infrastructure.E2ETest;
import org.auth.api.infrastructure.config.json.Json;
import org.auth.api.infrastructure.user.models.CreateUserRequest;
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

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class UserApiE2ETest {
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
    public void verifyContainerAndRepository() {
        assertTrue(MYSQL_CONTAINER.isRunning());
        userRepository.deleteAll();
    }

    @Test
    public void givenValidData_whenAccessesCreateUser_thenCreatesAnUser() throws Exception {
        // given
        final var requestContent = Json.marshal(new CreateUserRequest("test@mail.com", "test12"));
        assertEquals(0, userRepository.count());

        // when
        final var request = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        final var response = mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final var location = response.getHeader("Location");
        assertTrue(location.startsWith("/users/"));

        final var locationValue = location.replace("/users/", "");
        final var id = assertDoesNotThrow(() -> UUID.fromString(locationValue));

        assertEquals(1, userRepository.count());
        assertTrue(userRepository.findById(id).isPresent());
    }

    @Test
    public void givenNullData_whenAccessesCreateUser_thenReturnsAnUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new CreateUserRequest(null, null));
        assertEquals(0, userRepository.count());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.email[0].message", equalTo("email must not be null")))
                .andExpect(jsonPath("$.password[0].message", equalTo("password must not be null")));

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenEmptyData_whenAccessesCreateUser_thenReturnsAnUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new CreateUserRequest("", ""));
        assertEquals(0, userRepository.count());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.email[0].message", equalTo("email must not be empty")))
                .andExpect(jsonPath("$.password[0].message", equalTo("password must not be empty")));

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenInvalidEmail_whenAccessesCreateUser_thenReturnsAnUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new CreateUserRequest("test@mailcom", "test12"));
        assertEquals(0, userRepository.count());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.email[0].message", equalTo("email is invalid")));

        assertEquals(0, userRepository.count());
    }

    @Test
    public void givenPasswordLessThan5Characters_whenAccessesCreateUser_thenReturnsAnUnprocessableEntity() throws Exception {
        // given
        final var requestContent = Json.marshal(new CreateUserRequest("test@mail.com", "test1"));
        assertEquals(0, userRepository.count());

        // when
        final var actualRequest = post("/users/new")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);

        // then
        mvc.perform(actualRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.password[0].message", equalTo("password must have more than 5 characters")));

        assertEquals(0, userRepository.count());
    }
}
