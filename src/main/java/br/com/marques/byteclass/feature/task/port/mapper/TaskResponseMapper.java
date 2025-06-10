package br.com.marques.byteclass.feature.task.port.mapper;

import br.com.marques.byteclass.feature.task.port.dto.TaskDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.port.dto.TaskSummary;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface TaskResponseMapper {

    @Mapping(source = "courseId",  target = "courseId")
    @Mapping(source = "statement", target = "statement")
    @Mapping(source = "taskOrder", target = "taskOrder")
    @Mapping(source = "taskType",  target = "taskType")
    TaskSummary toDto(Task entity);

    @Mapping(source = "id",  target = "id")
    @Mapping(source = "courseId",  target = "courseId")
    @Mapping(source = "statement", target = "statement")
    @Mapping(source = "taskOrder", target = "taskOrder")
    @Mapping(source = "taskType",  target = "taskType")
    @Mapping(source = "choices",   target = "choices")
    TaskDetails toDetailsDto(Task entity);
}
