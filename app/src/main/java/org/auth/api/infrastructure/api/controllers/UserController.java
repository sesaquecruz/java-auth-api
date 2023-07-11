package org.auth.api.infrastructure.api.controllers;

import org.auth.api.application.user.create.CreateUser;
import org.auth.api.application.user.create.CreateUserInput;
import org.auth.api.application.user.find.FindUser;
import org.auth.api.application.user.find.FindUserInput;
import org.auth.api.application.user.update.UpdateUser;
import org.auth.api.application.user.update.UpdateUserInput;
import org.auth.api.infrastructure.api.UserApi;
import org.auth.api.infrastructure.services.security.AuthTokenService;
import org.auth.api.infrastructure.user.models.UserRequest;
import org.auth.api.infrastructure.user.models.UserResponse;
import org.auth.api.infrastructure.user.presenters.UserApiPresenter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {
    private final AuthTokenService authTokenService;
    private final CreateUser createUserUC;
    private final FindUser findUserUC;
    private final UpdateUser updateUserUC;

    public UserController(
            final AuthTokenService authTokenService,
            final CreateUser createUserUC,
            final FindUser findUserUC,
            final UpdateUser updateUserUC
    ) {
        this.authTokenService = authTokenService;
        this.createUserUC = createUserUC;
        this.findUserUC = findUserUC;
        this.updateUserUC = updateUserUC;
    }

    @Override
    public ResponseEntity<Void> createUser(final UserRequest body) {
        final var input = CreateUserInput.with(body.email(), body.password());
        createUserUC.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> loginUser(final Authentication authentication) {
        final var token = authTokenService.createToken(authentication);
        return ResponseEntity.ok()
                .header("Authorization", token)
                .build();
    }

    @Override
    public ResponseEntity<UserResponse> findUser(final String token) {
        final var sub = authTokenService.getSub(token);
        final var input = FindUserInput.with(sub);
        final var output = findUserUC.execute(input);
        return ResponseEntity.ok(UserApiPresenter.present(output));
    }

    @Override
    public ResponseEntity<Void> updateUser(final String token, final UserRequest body) {
        final var sub = authTokenService.getSub(token);
        final var input = UpdateUserInput.with(sub, body.email(), body.password());
        updateUserUC.execute(input);
        return ResponseEntity.noContent().build();
    }
}
