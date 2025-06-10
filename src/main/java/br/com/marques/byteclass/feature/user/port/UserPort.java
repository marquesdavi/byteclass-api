package br.com.marques.byteclass.feature.user.port;

import br.com.marques.byteclass.common.util.PageableRequest;
import br.com.marques.byteclass.feature.user.port.dto.UserDetailsInternal;
import br.com.marques.byteclass.feature.user.port.dto.UserRequest;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;

public interface UserPort {
    Long create(@Valid UserRequest request);
    void update(@Min(value = 1, message = "Id must be greater than 0") Long id, @Valid UserRequest request);
    void delete(@Min(value = 1, message = "Id must be greater than 0") Long id);
    UserSummary getById(@Min(value = 1, message = "Id must be greater than 0") Long id);
    Page<UserSummary> list(PageableRequest pageable);
    UserDetailsInternal findByEmail(String email);
}
