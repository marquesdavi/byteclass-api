package br.com.marques.byteclass.feature.user.app;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.user.domain.Role;
import br.com.marques.byteclass.feature.user.port.UserPort;
import br.com.marques.byteclass.feature.user.port.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.port.dto.TokenResponse;
import br.com.marques.byteclass.feature.user.port.dto.UserDetailsInternal;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    JwtEncoder jwtEncoder;
    @Mock
    UserPort userPort;
    @InjectMocks
    AuthenticationServiceImpl service;

    static class Fixtures {
        static LoginRequest loginRequest() {
            return new LoginRequest("foo@domain.com", "plainPass");
        }

        static UserDetailsInternal userDetails() {
            return new UserDetailsInternal(
                    42L,
                    "foo@domain.com",
                    "Foo User",
                    "encryptedPass",
                    Role.STUDENT
            );
        }

        static UserSummary userSummary() {
            return new UserSummary(
                    42L,
                    "Foo User",
                    "foo@domain.com",
                    Role.STUDENT
            );
        }
    }

    @BeforeEach
    void setUp() {
        // TODO document why this method is empty
    }

    @Nested
    @DisplayName("authenticate(request)")
    class AuthenticateTests {

        @Test
        @DisplayName("should return token when credentials are valid")
        void shouldAuthenticateSuccessfully() {
            var request = Fixtures.loginRequest();
            var user = Fixtures.userDetails();
            String fakeToken = "fake-jwt-token";
            long expiresIn = 3600L;
            Jwt fakeJwt = new Jwt(
                    fakeToken,
                    Instant.now(),
                    Instant.now().plusSeconds(expiresIn),
                    Map.of("alg", "none"),
                    Map.of("sub", "42")
            );

            when(userPort.findByEmail(request.email()))
                    .thenReturn(user);
            when(passwordEncoder.matches(request.password(), user.password()))
                    .thenReturn(true);
            when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                    .thenReturn(fakeJwt);

            TokenResponse response = service.authenticate(request);

            assertThat(response.accessToken()).isEqualTo(fakeToken);
            assertThat(response.expiresIn()).isEqualTo(expiresIn);
            verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
        }

        @Test
        @DisplayName("should throw BadCredentialsException when password is incorrect")
        void shouldThrowOnBadPassword() {
            var request = Fixtures.loginRequest();
            var user = Fixtures.userDetails();

            when(userPort.findByEmail(request.email()))
                    .thenReturn(user);
            when(passwordEncoder.matches(request.password(), user.password()))
                    .thenReturn(false);

            assertThatThrownBy(() -> service.authenticate(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Invalid email or password!");
        }

        @Test
        @DisplayName("should throw BadCredentialsException when user is null")
        void shouldThrowWhenUserNull() {
            var request = Fixtures.loginRequest();
            when(userPort.findByEmail(request.email()))
                    .thenReturn(null);

            assertThatThrownBy(() -> service.authenticate(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Invalid email or password!");
        }

        @Test
        @DisplayName("should propagate NotFoundException when userPort throws")
        void shouldPropagateNotFound() {
            var request = Fixtures.loginRequest();
            when(userPort.findByEmail(request.email()))
                    .thenThrow(new NotFoundException("User not found"));

            assertThatThrownBy(() -> service.authenticate(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("getAuthenticated()")
    class GetAuthenticatedTests {
        @Test
        @DisplayName("should return user summary for authenticated principal")
        void shouldReturnUserSummary() {
            var summary = Fixtures.userSummary();
            Authentication auth = mock(Authentication.class);
            when(auth.getName()).thenReturn("42");
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(userPort.getById(42L)).thenReturn(summary);

            UserSummary result = service.getAuthenticated();

            assertThat(result).isEqualTo(summary);
        }
    }
}
