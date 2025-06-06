package br.com.marques.byteclass.feature.user.controller;

import br.com.marques.byteclass.feature.auth.dto.LoginRequest;
import br.com.marques.byteclass.feature.auth.dto.TokenResponse;
import br.com.marques.byteclass.feature.auth.service.AuthenticationService;
import br.com.marques.byteclass.feature.user.dto.UserRequest;
import br.com.marques.byteclass.feature.user.dto.UserSummary;
import br.com.marques.byteclass.feature.user.entity.User;
import br.com.marques.byteclass.feature.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "User management")
public class UserController {
    private final UserService<User, UserRequest, UserSummary> service;
    private final AuthenticationService<User, LoginRequest, TokenResponse> authService;

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserRequest dto) {
        service.create(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Updates the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/me")
    public ResponseEntity<Void> update(@Valid @RequestBody UserRequest dto) {
        Long currentId = authService.getAuthenticated().getId();
        service.update(currentId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Gets the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Long currentId = authService.getAuthenticated().getId();
        User user = service.getByID(currentId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Deletes the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount() {
        Long currentId = authService.getAuthenticated().getId();
        service.deleteAccount(currentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all users (Staff Exclusive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> list() {
        List<User> users = service.list();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get users(collaborators) by board id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/board/{id}")
    public ResponseEntity<List<UserSummary>> listByBoard(@PathVariable("id") UUID id) {
        List<UserSummary> users = service.listByBoardId(id);
        return ResponseEntity.ok(users);
    }
}
