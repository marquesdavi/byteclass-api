package br.com.marques.byteclass.feature.user.api;

import br.com.marques.byteclass.feature.user.api.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.api.dto.TokenResponse;
import br.com.marques.byteclass.feature.user.api.dto.UserSummary;

public interface AuthenticationApi{
    TokenResponse authenticate(LoginRequest request);
    UserSummary getAuthenticated();
}
