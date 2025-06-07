package br.com.marques.byteclass.feature.user.service;

import br.com.marques.byteclass.config.resilience.Resilient;
import br.com.marques.byteclass.feature.user.api.AuthenticationApi;
import br.com.marques.byteclass.feature.user.api.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.api.dto.TokenResponse;
import br.com.marques.byteclass.feature.user.api.dto.UserSummary;
import br.com.marques.byteclass.feature.user.api.dto.UserDetailsInternal;
import br.com.marques.byteclass.feature.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationApi {
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final UserApi userApi;

    @Value("${jwt.token.expires-in:3600}")
    private long expiresIn;
    private static final String ISSUER = "byteclass-api";

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public TokenResponse authenticate(LoginRequest request) {
        UserDetailsInternal user = userApi.findByEmail(request.email());

        if (Objects.isNull(user) || !isPasswordCorrect(request.password(), user.password())) {
            throw new BadCredentialsException("Usuário ou senha inválidos!");
        }
        return generateResponse(user);
    }

    private boolean isPasswordCorrect(String requestPassword, String userPassword) {
        return passwordEncoder.matches(requestPassword, userPassword);
    }

    private TokenResponse generateResponse(UserDetailsInternal user) {
        JwtClaimsSet claims = buildJwtClaimsSet(user);
        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenResponse(jwtValue, expiresIn);
    }

    private JwtClaimsSet buildJwtClaimsSet(UserDetailsInternal user) {
        Instant now = Instant.now();

        return JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(user.id().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("role", user.role().name())
                .build();
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public UserSummary getAuthenticated(){
        Authentication currentSession = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(currentSession.getName());
        return userApi.getById(userId);
    }
}
