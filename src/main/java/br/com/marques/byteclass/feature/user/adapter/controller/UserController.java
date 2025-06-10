package br.com.marques.byteclass.feature.user.adapter.controller;

import br.com.marques.byteclass.feature.user.port.UserPort;
import br.com.marques.byteclass.feature.user.port.dto.UserRequest;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "Endpoints for managing users")
@RequiredArgsConstructor
public class UserController {
    private final UserPort userPort;

    @Operation(summary = "Create a new user", description = "Registers a new user with name, email, and password.")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    @PostMapping
    @CacheEvict(value = {"users", "userDetails"}, allEntries = true)
    public ResponseEntity<Long> create(
            @Valid @RequestBody UserRequest dto
    ) {
        Long id = userPort.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "List users with pagination",
            description = "Retrieves a page of registered users. Use `page`, `size` and `sort` query parameters."
    )
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    @Cacheable(value = "users")
    public Page<UserSummary> list(@ParameterObject Pageable pageable) {
        return userPort.list(pageable);
    }

    @Operation(summary = "Get user by ID", description = "Fetches details of a single user by their ID.")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    @Cacheable(value = "userDetails", key = "#id")
    public UserSummary get(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id
    ) {
        return userPort.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
    @Operation(summary = "Update a user",
            description = "Updates name, email, and/or password for an existing user.")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or email conflict")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PatchMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = "userDetails", key = "#id"),
            @CacheEvict(value = "users", allEntries = true)
    })
    public ResponseEntity<Void> update(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequest dto
    ) {
        userPort.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
    @Operation(summary = "Delete a user", description = "Soft-deletes or removes a user by ID.")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = "userDetails", key = "#id"),
            @CacheEvict(value = "users", allEntries = true)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id
    ) {
        userPort.delete(id);
        return ResponseEntity.ok().build();
    }
}
