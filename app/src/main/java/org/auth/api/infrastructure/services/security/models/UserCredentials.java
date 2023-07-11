package org.auth.api.infrastructure.services.security.models;

import org.auth.api.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserCredentials implements UserDetails {
    private final User user;

    private UserCredentials(final User user) {
        this.user = user;
    }

    public static UserCredentials with(final User user) {
        return new UserCredentials(user);
    }

    public String getId() {
        return user.getId().getValue();
    }

    @Override
    public String getUsername() {
        return user.getEmail().getAddress();
    }

    @Override
    public String getPassword() {
        return user.getPassword().getValue();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
