package org.auth.api.application.user.update;

public record UpdateUserInput(
        String id,
        String email,
        String password
) {
    public static UpdateUserInput with(
            final String id,
            final String email,
            final String password
    ) {
        return new UpdateUserInput(id, email, password);
    }
}
