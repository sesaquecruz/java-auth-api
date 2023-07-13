package org.auth.api.infrastructure.services.security;

import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.ValidationException;
import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.infrastructure.services.security.models.UserCredentials;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthUserService implements UserDetailsService {
    private static final String INVALID_CREDENTIALS = "invalid credentials";
    private final UserGateway userGateway;

    public AuthUserService(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserDetails loadUserByUsername(final String emailAddress) throws UsernameNotFoundException {
        Email email;
        Optional<User> user;

        try {
            email = Email.with(emailAddress);
        } catch (ValidationException ex) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        try {
            user = userGateway.findByEmail(email);
        } catch (Exception ex) {
            throw GatewayException.with(GatewayException.USER_GATEWAY_ERROR, ex);
        }

        if (user.isEmpty())
            throw new UsernameNotFoundException(INVALID_CREDENTIALS);

        return UserCredentials.with(user.get());
    }
}
