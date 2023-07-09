package org.auth.api.infrastructure.api.controllers;

import org.auth.api.application.user.CreateUser;
import org.auth.api.application.user.CreateUserInput;
import org.auth.api.infrastructure.api.UserApi;
import org.auth.api.infrastructure.user.models.CreateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class UserController implements UserApi {
    private final CreateUser createUserUC;

    public UserController(final CreateUser createUserUC) {
        this.createUserUC = createUserUC;
    }

    @Override
    public ResponseEntity<Void> createUser(final CreateUserRequest body) {
        final var input = CreateUserInput.with(body.email(), body.password());
        final var output = createUserUC.execute(input);
        return ResponseEntity.created(URI.create("/users/" + output.id())).build();
    }
}
