package org.auth.api.application.user.update;

import org.auth.api.application.UseCase;
import org.auth.api.domain.user.UserGateway;

public abstract class UpdateUser extends UseCase<UpdateUserInput, Void> {
    protected final UserGateway userGateway;

    protected UpdateUser(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }
}
