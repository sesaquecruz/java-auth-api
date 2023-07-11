package org.auth.api.application.user.find;

public record FindUserOutput(
        String id,
        String email
) {
    public static FindUserOutput with(final String id, final String email) {
        return new FindUserOutput(id, email);
    }
}
