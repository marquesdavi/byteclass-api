package br.com.marques.byteclass.feature.course.port.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.course.domain.Course;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface CourseResponseMapper {
    CourseSummary toDto(Course entity);
}
