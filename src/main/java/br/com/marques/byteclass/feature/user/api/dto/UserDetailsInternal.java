package br.com.marques.byteclass.feature.user.api.dto;

import br.com.marques.byteclass.feature.user.entity.Role;
import lombok.Builder;

@Builder
public record UserDetailsInternal(
        Long id,
        String name,
        String email,
        String password,
        Role role
) {
    public boolean isInstructor() {
        return this.role().name().equals(Role.INSTRUCTOR);
    }
}
