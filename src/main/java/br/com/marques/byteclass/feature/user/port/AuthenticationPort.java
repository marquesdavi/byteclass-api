package br.com.marques.byteclass.feature.user.port;

import br.com.marques.byteclass.feature.user.port.dto.LoginRequest;
import br.com.marques.byteclass.feature.user.port.dto.TokenResponse;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;

public interface AuthenticationPort {
    TokenResponse authenticate(LoginRequest request);
    UserSummary getAuthenticated();
}
