package br.com.marques.byteclass.feature.user.adapter.controller;

import br.com.marques.byteclass.common.exception.GenericException;
import br.com.marques.byteclass.feature.user.port.AuthenticationPort;
import br.com.marques.byteclass.feature.user.port.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.port.dto.TokenResponse;
import br.com.marques.byteclass.feature.util.UserTestUtils.LoginRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AuthenticationPort service;

    @Autowired
    ObjectMapper mapper;

    static class Fixtures {
        static LoginRequestBuilder valid() {
            return new LoginRequestBuilder();
        }

        static TokenResponse token(String jwt, long expires) {
            return new TokenResponse(jwt, expires);
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("200 → returns token on successful authentication")
        void should_return_token() throws Exception {
            LoginRequest req = Fixtures.valid().build();
            TokenResponse resp = Fixtures.token("jwt-token-abc", 3600L);

            when(service.authenticate(any(LoginRequest.class))).thenReturn(resp);

            mvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("jwt-token-abc"))
                    .andExpect(jsonPath("$.expiresIn").value(3600));
        }

        @ParameterizedTest(name = "#{index} → invalid payload yields 400")
        @MethodSource("invalidRequests")
        void should_reject_invalid_request(LoginRequest bad) throws Exception {
            mvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        static Stream<LoginRequest> invalidRequests() {
            return Stream.of(
                    new LoginRequest("", ""),                     // ambos vazios
                    new LoginRequest(null, "password"),           // email nulo
                    new LoginRequest("not-an-email", "password"), // formato inválido
                    new LoginRequest("a@b.com", null),            // senha nula
                    new LoginRequest("a@b.com", "")               // senha vazia
            );
        }

        @Test
        @DisplayName("400 → authentication failure propagates exception")
        void should_propagate_auth_failure() throws Exception {
            LoginRequest req = Fixtures.valid().build();
            when(service.authenticate(any(LoginRequest.class)))
                    .thenThrow(new GenericException("Bad credentials", HttpStatus.BAD_REQUEST));

            mvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Bad credentials"));
        }
    }
}
