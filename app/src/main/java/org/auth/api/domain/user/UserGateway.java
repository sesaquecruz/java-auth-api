package org.auth.api.domain.user;

import org.auth.api.domain.valueobjects.Email;

import java.util.Optional;

public interface UserGateway {
    User save(User user);
    Optional<User> findByEmail(Email email);
}
