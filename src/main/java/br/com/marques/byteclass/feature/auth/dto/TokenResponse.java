package br.com.marques.byteclass.feature.auth.dto;

public record TokenResponse(String accessToken, Long expiresIn) {
}
