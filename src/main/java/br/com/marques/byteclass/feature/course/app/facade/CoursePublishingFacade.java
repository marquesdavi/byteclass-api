package br.com.marques.byteclass.feature.course.app.facade;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.course.domain.Course;
import br.com.marques.byteclass.feature.course.domain.Status;
import br.com.marques.byteclass.feature.course.adapter.repository.CourseRepository;
import br.com.marques.byteclass.feature.task.port.TaskPort;
import br.com.marques.byteclass.feature.task.port.dto.TaskSummary;
import br.com.marques.byteclass.feature.task.domain.Type;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CoursePublishingFacade {
    private final CourseRepository courseRepository;
    private final TaskPort taskPort;

    @Transactional
    public void publishCourse(@Min(value = 1, message = "Id must be greater than 0") Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        ensureNotPublished(course);
        List<TaskSummary> tasks = taskPort.listByCourseId(courseId);
        ensureHasTasks(tasks);
        ensureSequentialOrder(tasks);
        ensureAllTypesPresent(tasks);

        course.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        course.setStatus(Status.PUBLISHED);
        courseRepository.save(course);
    }

    private void ensureNotPublished(Course course) {
        if (course.getStatus() == Status.PUBLISHED) {
            throw new AlreadyExistsException("Course already published");
        }
    }

    private void ensureHasTasks(List<TaskSummary> tasks) {
        if (tasks.isEmpty()) {
            throw new NotFoundException("Course must have at least one task");
        }
    }

    private void ensureSequentialOrder(List<TaskSummary> tasks) {
        List<Integer> actual = tasks.stream()
                .map(TaskSummary::taskOrder)
                .toList();
        List<Integer> expected = IntStream
                .rangeClosed(1, tasks.size())
                .boxed()
                .toList();

        if (!actual.equals(expected)) {
            throw new IllegalArgumentException("Tasks out of sequence");
        }
    }

    private void ensureAllTypesPresent(List<TaskSummary> tasks) {
        Set<Type> present = tasks.stream()
                .map(TaskSummary::taskType)
                .collect(Collectors.toSet());

        Set<Type> required = EnumSet.of(
                Type.OPEN_TEXT,
                Type.SINGLE_CHOICE,
                Type.MULTIPLE_CHOICE
        );

        if (!present.containsAll(required)) {
            throw new IllegalArgumentException(
                    "Course must include at least one of each task type"
            );
        }
    }
}
