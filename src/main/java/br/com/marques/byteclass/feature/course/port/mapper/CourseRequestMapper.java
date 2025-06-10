package br.com.marques.byteclass.feature.course.port.mapper;

import org.mapstruct.*;
import br.com.marques.byteclass.feature.course.port.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.domain.Course;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface CourseRequestMapper {

    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "instructorId", ignore = true)
    @Mapping(target = "status",       constant = "BUILDING")
    @Mapping(target = "publishedAt",  ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    Course toEntity(CourseRequest dto);
}
