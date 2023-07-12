package org.auth.api.application.user.delete;

import org.auth.api.application.UseCase;
import org.auth.api.domain.user.UserGateway;

public abstract class DeleteUser extends UseCase<DeleteUserInput, Void> {
    protected final UserGateway userGateway;

    protected DeleteUser(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }
}
