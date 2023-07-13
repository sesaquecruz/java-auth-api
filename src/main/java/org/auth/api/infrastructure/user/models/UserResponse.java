package org.auth.api.infrastructure.user.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email
) {
    public static UserResponse with(final String id, final String email) {
        return new UserResponse(id, email);
    }
}
