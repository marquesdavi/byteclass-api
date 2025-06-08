package br.com.marques.byteclass.feature.course.port.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.course.domain.Course;

@Mapper(componentModel = "spring")
public interface CourseResponseMapper {
    CourseSummary toDto(Course entity);
}
