package br.com.marques.byteclass.feature.task.port.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.task.port.dto.OptionDto;
import br.com.marques.byteclass.feature.task.domain.Choice;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface OptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "option")
    @Mapping(target = "task", ignore = true)
    Choice toEntity(OptionDto dto);
}
