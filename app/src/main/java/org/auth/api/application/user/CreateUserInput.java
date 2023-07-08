package org.auth.api.application.user;

public record CreateUserInput(
        String email,
        String password
) {
    public static CreateUserInput with(final String email, final String password) {
        return new CreateUserInput(email, password);
    }
}
