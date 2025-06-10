package br.com.marques.byteclass.feature.course.port;

import br.com.marques.byteclass.common.util.PageableRequest;
import br.com.marques.byteclass.feature.course.port.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;

public interface CoursePort {
    Long create(@Valid CourseRequest request);
    Page<CourseSummary> list(PageableRequest pageableRequest);
    CourseSummary getById(@Min(value = 1, message = "Id must be greater than 0") Long id);
    void update(@Min(value = 1, message = "Id must be greater than 0") Long id, CourseRequest request);
    void delete(@Min(value = 1, message = "Id must be greater than 0") Long id);
    void publish(@Min(value = 1, message = "Id must be greater than 0") Long id);
}
