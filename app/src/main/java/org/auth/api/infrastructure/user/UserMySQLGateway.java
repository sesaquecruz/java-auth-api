package org.auth.api.infrastructure.user;

import org.auth.api.domain.user.User;
import org.auth.api.domain.user.UserGateway;
import org.auth.api.domain.valueobjects.Email;
import org.auth.api.infrastructure.user.persitence.UserJpaEntity;
import org.auth.api.infrastructure.user.persitence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserMySQLGateway implements UserGateway {
    private final UserRepository userRepository;

    public UserMySQLGateway(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(final User user) {
        return userRepository
                .saveAndFlush(UserJpaEntity.from(user))
                .toAggregate();
    }

    @Override
    public Optional<User> findByEmail(final Email email) {
        return userRepository
                .findByEmail(email.getAddress())
                .map(UserJpaEntity::toAggregate);
    }
}
