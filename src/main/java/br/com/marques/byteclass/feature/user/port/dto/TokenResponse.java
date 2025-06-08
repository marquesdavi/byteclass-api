package br.com.marques.byteclass.feature.user.port.dto;

public record TokenResponse(String accessToken, Long expiresIn) {
}
