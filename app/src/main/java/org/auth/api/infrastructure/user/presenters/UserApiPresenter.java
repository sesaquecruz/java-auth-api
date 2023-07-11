package org.auth.api.infrastructure.user.presenters;

import org.auth.api.application.user.find.FindUserOutput;
import org.auth.api.infrastructure.user.models.UserResponse;

public interface UserApiPresenter {
    static UserResponse present(final FindUserOutput output) {
        return UserResponse.with(
                output.id(),
                output.email()
        );
    }
}
