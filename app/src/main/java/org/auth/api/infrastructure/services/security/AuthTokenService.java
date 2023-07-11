package org.auth.api.infrastructure.services.security;

import org.auth.api.infrastructure.services.security.models.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class AuthTokenService {
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiry}")
    private long expiry;
    private final String TOKEN_PREFIX = "Bearer ";
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public AuthTokenService(final JwtEncoder jwtEncoder, final JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String createToken(final Authentication authentication) {
        final var now = Instant.now();
        final var credentials = (UserCredentials) authentication.getPrincipal();

        final var scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        final var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(credentials.getId())
                .claim("scope", scope)
                .build();

        return TOKEN_PREFIX + this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String getSub(final String token) {
        return jwtDecoder.decode(token.substring(TOKEN_PREFIX.length()))
                .getSubject();
    }
}
