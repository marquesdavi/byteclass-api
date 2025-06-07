package br.com.marques.byteclass.feature.user.api;

import br.com.marques.byteclass.feature.user.api.dto.UserRequest;
import br.com.marques.byteclass.feature.user.api.dto.UserSummary;
import br.com.marques.byteclass.feature.user.api.dto.UserDetailsInternal;

import java.util.List;

public interface UserApi {
    Long create(UserRequest request);
    void update(Long id, UserRequest request);
    void delete(Long id);
    UserSummary getById(Long id);
    List<UserSummary> list();
    UserDetailsInternal findByEmail(String email);
}
