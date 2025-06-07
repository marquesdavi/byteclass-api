package br.com.marques.byteclass.feature.user.api.mapper;

import br.com.marques.byteclass.feature.user.api.dto.UserDetailsInternal;
import org.mapstruct.*;
import br.com.marques.byteclass.feature.user.entity.User;
import br.com.marques.byteclass.feature.user.api.dto.UserSummary;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {
    UserSummary toDto(User entity);
    UserDetailsInternal toInternalDto(User entity);
}

