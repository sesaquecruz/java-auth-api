package org.auth.api.infrastructure.config;

import org.auth.api.domain.user.User;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Password;
import org.auth.api.infrastructure.services.security.AuthUserService;
import org.auth.api.infrastructure.services.security.models.UserCredentials;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("test-integration")
@Configuration
public class UserDetailsConfig {
    public static final String USER_EMAIL = "test@mail.com";
    public static final String USER_PASSWORD = "test123";
    private static final User USER = User.newUser(Email.with(USER_EMAIL), Password.withRawValue(USER_PASSWORD));
    public static final String USER_ID = USER.getId().getValue();

    @Bean
    public UserDetailsService userDetailsService(final PasswordEncoder encoder) {
        USER.updatePassword(Password.withEncodedValue(encoder.encode(USER_PASSWORD)));

        final var userCredentials = UserCredentials.with(USER);
        final var authUserService = Mockito.mock(AuthUserService.class);

        BDDMockito.willReturn(userCredentials).given(authUserService).loadUserByUsername(USER_EMAIL);

        return authUserService;
    }
}
