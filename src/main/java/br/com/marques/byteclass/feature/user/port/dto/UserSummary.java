package br.com.marques.byteclass.feature.user.port.dto;

import br.com.marques.byteclass.feature.user.domain.Role;
import lombok.Builder;

@Builder
public record UserSummary(
        Long id,
        String name,
        String email,
        Role role
) {
    public boolean isInstructor() {
        return this.role().name().equals(Role.INSTRUCTOR.name());
    }
}
