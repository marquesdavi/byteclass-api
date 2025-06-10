package br.com.marques.byteclass.feature.task.adapter.repository;

import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.domain.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    static class Fixtures {
        static Task build(Long courseId, String statement, Integer order, Type type) {
            return Task.builder()
                    .courseId(courseId)
                    .statement(statement)
                    .taskOrder(order)
                    .taskType(type)
                    .build();
        }
    }

    @Nested
    @DisplayName("findTaskByStatement(String)")
    class FindByStatement {
        @Test
        @DisplayName("returns task when statement exists")
        void shouldReturnTaskWhenExists() {
            Task t = Fixtures.build(1L, "Unique Statement", 1, Type.OPEN_TEXT);
            taskRepository.save(t);

            Optional<Task> result = taskRepository.findTaskByStatement("Unique Statement");

            assertThat(result).isPresent()
                    .get()
                    .extracting(Task::getStatement)
                    .isEqualTo("Unique Statement");
        }

        @Test
        @DisplayName("returns empty when statement not found")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Task> result = taskRepository.findTaskByStatement("No Such");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByCourseIdOrderByTaskOrder(Long)")
    class FindAllByCourse {
        @Test
        @DisplayName("returns list ordered by taskOrder")
        void shouldReturnOrderedList() {
            // given: three tasks out of order
            Task t1 = Fixtures.build(2L, "Task A", 2, Type.SINGLE_CHOICE);
            Task t2 = Fixtures.build(2L, "Task B", 1, Type.OPEN_TEXT);
            Task t3 = Fixtures.build(3L, "Other Course", 1, Type.OPEN_TEXT);
            taskRepository.saveAll(List.of(t1, t2, t3));

            // when
            List<Task> result = taskRepository.findAllByCourseIdOrderByTaskOrder(2L);

            // then: only t2 then t1
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getStatement()).isEqualTo("Task B");
            assertThat(result.get(1).getStatement()).isEqualTo("Task A");
        }
    }
}
