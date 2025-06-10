package br.com.marques.byteclass.feature.task.app;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.task.adapter.repository.TaskRepository;
import br.com.marques.byteclass.feature.task.app.strategy.TaskTypeStrategy;
import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.port.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.port.dto.OpenTextRequest;
import br.com.marques.byteclass.feature.task.port.dto.TaskDetails;
import br.com.marques.byteclass.feature.task.port.dto.TaskSummary;
import br.com.marques.byteclass.feature.task.port.mapper.TaskResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskTypeStrategy openStrategy;
    @Mock
    TaskTypeStrategy singleStrategy;
    @Mock
    TaskTypeStrategy multiStrategy;
    @Mock
    TaskRepository taskRepository;
    @Mock
    TaskResponseMapper mapper;

    TaskServiceImpl service;

    static class Fixtures {
        static OpenTextRequest openRequest() {
            OpenTextRequest r = new OpenTextRequest();
            r.setCourseId(1L);
            r.setStatement("Open statement");
            r.setOrder(1);
            return r;
        }

        static ChoiceRequest choiceRequest() {
            ChoiceRequest r = new ChoiceRequest();
            r.setCourseId(2L);
            r.setStatement("Choice statement");
            r.setOrder(2);
            return r;
        }

        static Task task(long id, long courseId, String stmt, int order, Type type) {
            return Task.builder()
                    .id(id)
                    .courseId(courseId)
                    .statement(stmt)
                    .taskOrder(order)
                    .taskType(type)
                    .build();
        }

        static TaskSummary summary(Task t) {
            return new TaskSummary(t.getCourseId(), t.getStatement(), t.getTaskOrder(), t.getTaskType());
        }

        static TaskDetails details(Task t) {
            return new TaskDetails(t.getId(), t.getCourseId(), t.getStatement(), t.getTaskOrder(), t.getTaskType(), List.of());
        }
    }

    @BeforeEach
    void setUp() {
        when(openStrategy.getType()).thenReturn(Type.OPEN_TEXT);
        when(singleStrategy.getType()).thenReturn(Type.SINGLE_CHOICE);
        when(multiStrategy.getType()).thenReturn(Type.MULTIPLE_CHOICE);
        service = new TaskServiceImpl(
                List.of(openStrategy, singleStrategy, multiStrategy),
                taskRepository,
                mapper
        );
    }

    @Nested
    @DisplayName("createOpenText(request)")
    class CreateOpenText {
        @Test
        @DisplayName("delegates to OPEN_TEXT strategy")
        void shouldDelegateToOpenStrategy() {
            var req = Fixtures.openRequest();
            service.createOpenText(req);
            verify(openStrategy).save(req);
            verifyNoMoreInteractions(singleStrategy, multiStrategy);
        }
    }

    @Nested
    @DisplayName("createSingleChoice(request)")
    class CreateSingleChoice {
        @Test
        @DisplayName("delegates to SINGLE_CHOICE strategy")
        void shouldDelegateToSingleStrategy() {
            var req = Fixtures.choiceRequest();
            service.createSingleChoice(req);
            verify(singleStrategy).save(req);
            verifyNoMoreInteractions(openStrategy, multiStrategy);
        }
    }

    @Nested
    @DisplayName("createMultipleChoice(request)")
    class CreateMultipleChoice {
        @Test
        @DisplayName("delegates to MULTIPLE_CHOICE strategy")
        void shouldDelegateToMultiStrategy() {
            var req = Fixtures.choiceRequest();
            service.createMultipleChoice(req);
            verify(multiStrategy).save(req);
            verifyNoMoreInteractions(openStrategy, singleStrategy);
        }
    }

    @Nested
    @DisplayName("listByCourseId(courseId)")
    class ListByCourseId {
        @Test
        @DisplayName("returns mapped summaries in order")
        void shouldListSummaries() {
            Task t1 = Fixtures.task(1, 10, "A", 1, Type.OPEN_TEXT);
            Task t2 = Fixtures.task(2, 10, "B", 2, Type.SINGLE_CHOICE);
            when(taskRepository.findAllByCourseIdOrderByTaskOrder(10L)).thenReturn(List.of(t1, t2));
            TaskSummary s1 = Fixtures.summary(t1);
            TaskSummary s2 = Fixtures.summary(t2);
            when(mapper.toDto(t1)).thenReturn(s1);
            when(mapper.toDto(t2)).thenReturn(s2);

            var result = service.listByCourseId(10L);

            assertThat(result).containsExactly(s1, s2);
        }

        @Test
        @DisplayName("returns empty list when none found")
        void shouldReturnEmptyWhenNoTasks() {
            when(taskRepository.findAllByCourseIdOrderByTaskOrder(5L)).thenReturn(List.of());
            var result = service.listByCourseId(5L);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getById(id)")
    class GetById {
        @Test
        @DisplayName("returns mapped details when found")
        void shouldReturnDetails() {
            Task t = Fixtures.task(5, 20, "Detail", 3, Type.MULTIPLE_CHOICE);
            when(taskRepository.findById(5L)).thenReturn(Optional.of(t));
            TaskDetails det = Fixtures.details(t);
            when(mapper.toDetailsDto(t)).thenReturn(det);

            var result = service.getById(5L);

            assertThat(result).isEqualTo(det);
        }

        @Test
        @DisplayName("throws NotFoundException when missing")
        void shouldThrowWhenNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.getById(99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Task not found");
        }
    }
}
