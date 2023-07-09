package org.auth.api.infrastructure.config;

import org.auth.api.application.user.CreateUser;
import org.auth.api.application.user.DefaultCreateUser;
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
}
