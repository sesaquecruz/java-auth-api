package org.auth.api.application.user.delete;

public record DeleteUserInput(
        String id
) {
    public static DeleteUserInput with(final String id) {
        return new DeleteUserInput(id);
    }
}
