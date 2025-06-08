package br.com.marques.byteclass.feature.task.api.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.task.api.dto.*;
import br.com.marques.byteclass.feature.task.entity.*;
import br.com.marques.byteclass.feature.course.entity.Course;

@Mapper(componentModel = "spring")
public interface TaskRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "choices", ignore = true)
    @Mapping(target = "taskType", expression = "java(Type.OPEN_TEXT)")
    Task toOpenTextEntity(OpenTextRequest dto, @Context Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "choices", ignore = true)
    @Mapping(target = "taskType", expression = "java(Type.SINGLE_CHOICE)")
    Task toSingleChoiceEntity(ChoiceRequest dto, @Context Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "choices", ignore = true)
    @Mapping(target = "taskType", expression = "java(Type.MULTIPLE_CHOICE)")
    Task toMultipleChoiceEntity(ChoiceRequest dto, @Context Course course);
}
