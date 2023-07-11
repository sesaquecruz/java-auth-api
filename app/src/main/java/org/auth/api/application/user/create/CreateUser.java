package org.auth.api.application.user.create;

import org.auth.api.application.UseCase;
import org.auth.api.domain.user.UserGateway;

public abstract class CreateUser extends UseCase<CreateUserInput, CreateUserOutput> {
    protected final UserGateway userGateway;

    protected CreateUser(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }
}
