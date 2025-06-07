package br.com.marques.byteclass.feature.user.api.dto;

public record TokenResponse(String accessToken, Long expiresIn) {
}
