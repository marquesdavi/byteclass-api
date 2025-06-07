package br.com.marques.byteclass.feature.user.api.dto;

import br.com.marques.byteclass.feature.user.entity.Role;
import br.com.marques.byteclass.feature.user.entity.User;
import lombok.Builder;

import java.util.Objects;

@Builder
public record UserSummary(
        Long id,
        String name,
        String email,
        Role role
) {
    public boolean isInstructor() {
        return this.role().name().equals(Role.INSTRUCTOR);
    }
}
