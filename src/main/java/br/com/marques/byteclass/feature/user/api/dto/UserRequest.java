package br.com.marques.byteclass.feature.user.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequest(
        @NotNull
        @NotBlank
        @Size(min = 3, max = 70)
        String name,
        @Email
        @NotNull
        @NotBlank
        @Size(min = 8, max = 100)
        String email,
        @NotNull
        @NotBlank
        @Size(min = 8, max = 30)
        String password
) {
}
