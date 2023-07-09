package org.auth.api.application.user;

public record CreateUserOutput(
        String id
) {
    public static CreateUserOutput with(final String id) {
        return new CreateUserOutput(id);
    }
}
