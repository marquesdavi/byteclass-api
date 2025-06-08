package br.com.marques.byteclass.feature.user.port.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.user.port.dto.UserRequest;
import br.com.marques.byteclass.feature.user.domain.User;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "role",        constant = "STUDENT")
    @Mapping(target = "createdAt",   ignore = true)
    User toEntity(UserRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "role",        ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    void updateEntityFromDto(UserRequest dto, @MappingTarget User entity);
}
