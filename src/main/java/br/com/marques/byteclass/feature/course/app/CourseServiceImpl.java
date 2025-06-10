package br.com.marques.byteclass.feature.course.app;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.common.util.PageableRequest;
import br.com.marques.byteclass.config.resilience.Resilient;
import br.com.marques.byteclass.feature.course.adapter.repository.CourseRepository;
import br.com.marques.byteclass.feature.course.app.facade.CoursePublishingFacade;
import br.com.marques.byteclass.feature.course.domain.Course;
import br.com.marques.byteclass.feature.course.port.CoursePort;
import br.com.marques.byteclass.feature.course.port.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.course.port.mapper.CourseRequestMapper;
import br.com.marques.byteclass.feature.course.port.mapper.CourseResponseMapper;
import br.com.marques.byteclass.feature.user.port.AuthenticationPort;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CourseServiceImpl implements CoursePort {
    private final CourseRepository courseRepository;
    private final AuthenticationPort authApi;
    private final CourseRequestMapper requestMapper;
    private final CourseResponseMapper summaryMapper;
    private final CoursePublishingFacade publishingFacade;

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public Long create(@Valid CourseRequest dto) {
        UserSummary user = authApi.getAuthenticated();
        Course course = requestMapper.toEntity(dto);
        course.setInstructorId(user.id());
        return courseRepository.save(course).getId();
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public Page<CourseSummary> list(PageableRequest pageableRequest) {
        var pageable = pageableRequest.toPageable();
        return courseRepository.findAll(pageable)
            .map(summaryMapper::toDto);
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public CourseSummary getById(@Min(value = 1, message = "Id must be greater than 0") Long id) {
        return summaryMapper.toDto(findCourseOrThrow(id));
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void update(@Min(value = 1, message = "Id must be greater than 0") Long id, CourseRequest dto) {
        Course course = findCourseOrThrow(id);
        course.setTitle(dto.title());
        course.setDescription(dto.description());
        courseRepository.save(course);
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void delete(@Min(value = 1, message = "Id must be greater than 0") Long id) {
        courseRepository.delete(findCourseOrThrow(id));
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void publish(@Min(value = 1, message = "Id must be greater than 0") Long id) {
        publishingFacade.publishCourse(id);
    }

    private Course findCourseOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }
}
