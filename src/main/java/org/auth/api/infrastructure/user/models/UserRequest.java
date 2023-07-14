package org.auth.api.infrastructure.user.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRequest(
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {
}
