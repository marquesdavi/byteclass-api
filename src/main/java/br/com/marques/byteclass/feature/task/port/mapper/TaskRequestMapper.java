package br.com.marques.byteclass.feature.task.port.mapper;

import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.port.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.port.dto.OpenTextRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Mapper(
        componentModel = "spring",
        imports = { Type.class }
)
@Primary
public interface TaskRequestMapper {
    @Mapping(target = "id",        ignore = true)
    @Mapping(source = "courseId",  target = "courseId")
    @Mapping(source = "statement", target = "statement")
    @Mapping(source = "order",     target = "taskOrder")
    @Mapping(target = "choices",   ignore = true)
    @Mapping(target = "taskType",  expression = "java(Type.OPEN_TEXT)")
    @Mapping(target = "createdAt", ignore = true)
    Task toOpenTextEntity(OpenTextRequest dto);

    @Mapping(target = "id",        ignore = true)
    @Mapping(source = "courseId",  target = "courseId")
    @Mapping(source = "statement", target = "statement")
    @Mapping(source = "order",     target = "taskOrder")
    @Mapping(target = "choices",   ignore = true)
    @Mapping(target = "taskType",  expression = "java(Type.SINGLE_CHOICE)")
    @Mapping(target = "createdAt", ignore = true)
    Task toSingleChoiceEntity(ChoiceRequest dto);

    @Mapping(target = "id",        ignore = true)
    @Mapping(source = "courseId",  target = "courseId")
    @Mapping(source = "statement", target = "statement")
    @Mapping(source = "order",     target = "taskOrder")
    @Mapping(target = "choices",   ignore = true)
    @Mapping(target = "taskType",  expression = "java(Type.MULTIPLE_CHOICE)")
    @Mapping(target = "createdAt", ignore = true)
    Task toMultipleChoiceEntity(ChoiceRequest dto);
}
