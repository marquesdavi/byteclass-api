package br.com.marques.byteclass.feature.user.adapter.controller;

import br.com.marques.byteclass.feature.user.port.AuthenticationPort;
import br.com.marques.byteclass.feature.user.port.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.port.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "User Authentication", description = "User Authentication management")
public class AuthController {
    private final AuthenticationPort service;

    @Operation(summary = "Returns a JWT token")
    @ApiResponse(responseCode = "200", description = "User logged in")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(service.authenticate(loginRequest));
    }
}
