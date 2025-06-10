package br.com.marques.byteclass.feature.user.port.mapper;

import br.com.marques.byteclass.feature.user.port.dto.UserDetailsInternal;
import org.mapstruct.*;
import br.com.marques.byteclass.feature.user.domain.User;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    UserSummary toDto(User entity);
    UserDetailsInternal toInternalDto(User entity);
}

