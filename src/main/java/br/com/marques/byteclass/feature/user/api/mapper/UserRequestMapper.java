package br.com.marques.byteclass.feature.user.api.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.user.api.dto.UserRequest;
import br.com.marques.byteclass.feature.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {
    @Mapping(target = "id",    ignore = true)
    @Mapping(target = "role",  constant = "STUDENT")
    @Mapping(target = "isActive", constant = "true")
    User toEntity(UserRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserRequest dto, @MappingTarget User entity);
}
