package br.com.marques.byteclass.feature.user.controller;

import br.com.marques.byteclass.feature.user.api.UserApi;
import br.com.marques.byteclass.feature.user.api.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name="User")
@RequiredArgsConstructor
public class UserController {

    private final UserApi userApi;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody UserRequest dto) {
        Long id = userApi.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public List<UserSummary> list() {
        return userApi.list();
    }

    @GetMapping("/{id}")
    public UserSummary get(@PathVariable Long id) {
        return userApi.getById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest dto
    ) {
        userApi.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userApi.delete(id);
        return ResponseEntity.ok().build();
    }
}
