package br.com.marques.byteclass.feature.task.api.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.task.api.dto.OptionDto;
import br.com.marques.byteclass.feature.task.entity.Choice;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "option")
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Choice toEntity(OptionDto dto);
}
