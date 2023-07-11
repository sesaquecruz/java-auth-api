package org.auth.api.infrastructure.config;

import org.auth.api.application.user.create.CreateUser;
import org.auth.api.application.user.create.DefaultCreateUser;
import org.auth.api.application.user.find.DefaultFindUser;
import org.auth.api.application.user.find.FindUser;
import org.auth.api.application.user.update.DefaultUpdateUser;
import org.auth.api.application.user.update.UpdateUser;
import org.auth.api.domain.user.UserGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
    private final UserGateway userGateway;

    public UseCasesConfig(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Bean
    public CreateUser createUser() {
        return new DefaultCreateUser(userGateway);
    }

    @Bean
    public FindUser findUser() {
        return new DefaultFindUser(userGateway);
    }

    @Bean
    public UpdateUser updateUser() {
        return new DefaultUpdateUser(userGateway);
    }
}
