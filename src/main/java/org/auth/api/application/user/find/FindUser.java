package org.auth.api.application.user.find;

import org.auth.api.application.UseCase;
import org.auth.api.domain.user.UserGateway;

public abstract class FindUser extends UseCase<FindUserInput, FindUserOutput> {
    protected final UserGateway userGateway;

    protected FindUser(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }
}
