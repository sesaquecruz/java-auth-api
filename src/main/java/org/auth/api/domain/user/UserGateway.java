package org.auth.api.domain.user;

import org.auth.api.domain.valueobjects.Email;
import org.auth.api.domain.valueobjects.Identifier;

import java.util.Optional;

public interface UserGateway {
    User save(User user);
    Optional<User> findById(Identifier id);
    Optional<User> findByEmail(Email email);
    void deleteById(Identifier id);
}
