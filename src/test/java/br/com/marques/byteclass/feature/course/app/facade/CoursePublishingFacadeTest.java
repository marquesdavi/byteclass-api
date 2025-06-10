package br.com.marques.byteclass.feature.course.app.facade;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.course.adapter.repository.CourseRepository;
import br.com.marques.byteclass.feature.course.domain.Course;
import br.com.marques.byteclass.feature.course.domain.Status;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.port.TaskPort;
import br.com.marques.byteclass.feature.task.port.dto.TaskSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoursePublishingFacadeTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TaskPort taskPort;
    @InjectMocks
    private CoursePublishingFacade facade;
    @Captor
    private ArgumentCaptor<Course> courseCaptor;

    static class Fixtures {
        static Course domainCourse(Long id, Status status) {
            Course c = new Course();
            c.setId(id);
            c.setStatus(status);
            return c;
        }

        static TaskSummary ts(int order, Type type) {
            return new TaskSummary(null, "", order, type);
        }

        static List<TaskSummary> validTasks() {
            return List.of(
                    ts(1, Type.OPEN_TEXT),
                    ts(2, Type.SINGLE_CHOICE),
                    ts(3, Type.MULTIPLE_CHOICE)
            );
        }
    }

    @Nested
    @DisplayName("publishCourse(Long)")
    class PublishCourse {

        @Test
        @DisplayName("200 → successfully publishes a BUILDING course")
        void should_publish_when_all_conditions_met() {
            Course building = Fixtures.domainCourse(1L, Status.BUILDING);
            when(courseRepository.findById(1L)).thenReturn(Optional.of(building));
            when(taskPort.listByCourseId(1L)).thenReturn(Fixtures.validTasks());

            facade.publishCourse(1L);

            verify(courseRepository).save(courseCaptor.capture());
            Course saved = courseCaptor.getValue();
            assertThat(saved.getStatus()).isEqualTo(Status.PUBLISHED);
            assertThat(saved.getPublishedAt())
                    .isNotNull()
                    .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        }

        @Test
        @DisplayName("404 → course not found")
        void should_throw_not_found_if_course_missing() {
            when(courseRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> facade.publishCourse(99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Course not found");

            verifyNoInteractions(taskPort);
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("409 → already published")
        void should_throw_already_exists_if_already_published() {
            Course published = Fixtures.domainCourse(2L, Status.PUBLISHED);
            when(courseRepository.findById(2L)).thenReturn(Optional.of(published));

            assertThatThrownBy(() -> facade.publishCourse(2L))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessage("Course already published");

            verifyNoInteractions(taskPort);
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("404 → no tasks")
        void should_throw_not_found_if_no_tasks() {
            Course building = Fixtures.domainCourse(3L, Status.BUILDING);
            when(courseRepository.findById(3L)).thenReturn(Optional.of(building));
            when(taskPort.listByCourseId(3L)).thenReturn(List.of());

            assertThatThrownBy(() -> facade.publishCourse(3L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Course must have at least one task");

            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("400 → tasks out of sequence")
        void should_throw_illegal_argument_if_out_of_sequence() {
            Course building = Fixtures.domainCourse(4L, Status.BUILDING);
            when(courseRepository.findById(4L)).thenReturn(Optional.of(building));

            List<TaskSummary> badSequence = List.of(
                    Fixtures.ts(1, Type.OPEN_TEXT),
                    Fixtures.ts(3, Type.SINGLE_CHOICE),
                    Fixtures.ts(2, Type.MULTIPLE_CHOICE)
            );
            when(taskPort.listByCourseId(4L)).thenReturn(badSequence);

            assertThatThrownBy(() -> facade.publishCourse(4L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Tasks out of sequence");

            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("400 → missing at least one task type")
        void should_throw_illegal_argument_if_missing_type() {
            Course building = Fixtures.domainCourse(5L, Status.BUILDING);
            when(courseRepository.findById(5L)).thenReturn(Optional.of(building));

            List<TaskSummary> missing = List.of(
                    Fixtures.ts(1, Type.OPEN_TEXT),
                    Fixtures.ts(2, Type.SINGLE_CHOICE)
            );
            when(taskPort.listByCourseId(5L)).thenReturn(missing);

            assertThatThrownBy(() -> facade.publishCourse(5L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Course must include at least one of each task type");

            verify(courseRepository, never()).save(any());
        }
    }
}
