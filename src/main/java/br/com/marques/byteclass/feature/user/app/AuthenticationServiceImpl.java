package br.com.marques.byteclass.feature.user.app;

import br.com.marques.byteclass.config.resilience.Resilient;
import br.com.marques.byteclass.feature.user.port.AuthenticationPort;
import br.com.marques.byteclass.feature.user.port.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.port.dto.TokenResponse;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import br.com.marques.byteclass.feature.user.port.dto.UserDetailsInternal;
import br.com.marques.byteclass.feature.user.port.UserPort;
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
public class AuthenticationServiceImpl implements AuthenticationPort {
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final UserPort userPort;

    @Value("${jwt.token.expires-in:3600}")
    private long expiresIn = 3600L;
    private static final String ISSUER = "byteclass-api";

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public TokenResponse authenticate(LoginRequest request) {
        UserDetailsInternal user = userPort.findByEmail(request.email());

        if (Objects.isNull(user) || !isPasswordCorrect(request.password(), user.password())) {
            throw new BadCredentialsException("Invalid email or password!");
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
        return userPort.getById(userId);
    }
}
