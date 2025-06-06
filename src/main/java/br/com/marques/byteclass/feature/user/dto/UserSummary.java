package br.com.marques.byteclass.feature.user.dto;

import br.com.marques.byteclass.feature.user.entity.Role;

public record UserSummary(
        String name,
        String email,
        Role role
) {
}
