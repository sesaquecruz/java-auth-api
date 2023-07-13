package org.auth.api.application.user.find;

public record FindUserInput(
        String id
) {
    public static FindUserInput with(final String id) {
        return new FindUserInput(id);
    }
}
