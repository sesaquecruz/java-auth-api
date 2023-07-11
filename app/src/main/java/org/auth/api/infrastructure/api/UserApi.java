package org.auth.api.infrastructure.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.auth.api.infrastructure.user.models.CreateUserRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "users")
@Tag(name = "Users")
public interface UserApi {
    @PostMapping(
            value = "new",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User Created"),
            @ApiResponse(responseCode = "422", description = "Invalid Data"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    ResponseEntity<?> createUser(@RequestBody CreateUserRequest body);

    @PostMapping(value = "login")
    @Operation(
            summary = "User login",
            security = @SecurityRequirement(name = "Basic")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid Credentials"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    ResponseEntity<?> loginUser(Authentication authentication);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Find an user",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Found"),
            @ApiResponse(responseCode = "401", description = "Invalid Credentials"),
            @ApiResponse(responseCode = "500", description = "Server Error")
    })
    ResponseEntity<?> findUser(@RequestHeader(name="Authorization") @Schema(hidden = true) String token);
}
