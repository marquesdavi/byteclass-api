package br.com.marques.byteclass.feature.task.app.strategy;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.GenericException;
import br.com.marques.byteclass.feature.course.domain.Status;
import br.com.marques.byteclass.feature.course.port.CoursePort;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.task.adapter.repository.TaskRepository;
import br.com.marques.byteclass.feature.task.domain.Choice;
import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.port.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.port.dto.OpenTextRequest;
import br.com.marques.byteclass.feature.task.port.dto.OptionDto;
import br.com.marques.byteclass.feature.task.port.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.port.mapper.OptionMapper;
import br.com.marques.byteclass.feature.task.port.mapper.TaskRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStrategyTest {

    @Mock
    TaskRepository taskRepository;
    @Mock
    OptionMapper optionMapper;
    @Mock
    CoursePort courseApi;
    @Mock
    TaskRequestMapper requestMapper;

    AbstractTaskStrategy testStrategy;

    static class Fixtures {
        static Task t1(int order) {
            Task t = new Task();
            t.setTaskOrder(order);
            return t;
        }

        static OptionDto opt(String text, boolean correct) {
            OptionDto o = new OptionDto();
            o.setOption(text);
            o.setIsCorrect(correct);
            return o;
        }

        static ChoiceRequest choiceReq(int courseId, int order, OptionDto... opts) {
            ChoiceRequest r = new ChoiceRequest();
            r.setCourseId((long) courseId);
            r.setStatement("stmt");
            r.setOrder(order);
            r.setOptions(List.of(opts));
            return r;
        }

        static OpenTextRequest openReq(int courseId, int order) {
            OpenTextRequest r = new OpenTextRequest();
            r.setCourseId((long) courseId);
            r.setStatement("stmt");
            r.setOrder(order);
            return r;
        }

        static CourseSummary buildingCourse(long id) {
            return new CourseSummary(id, "x", "y", Status.BUILDING);
        }

        static CourseSummary publishedCourse(long id) {
            return new CourseSummary(id, "x", "y", Status.PUBLISHED);
        }
    }

    @BeforeEach
    void setUp() {
        testStrategy = new AbstractTaskStrategy(taskRepository, optionMapper, courseApi) {
            @Override
            public Type getType() {
                return Type.OPEN_TEXT;
            }

            @Override
            public void save(TaskRequest dto) { /*no-op*/ }
        };
    }

    @Nested
    @DisplayName("validateStatement")
    class ValidateStatement {
        @Test
        @DisplayName("no exception when none found")
        void noError() {
            when(taskRepository.findTaskByStatement("ok")).thenReturn(Optional.empty());
            testStrategy.validateStatement("ok");
        }

        @Test
        @DisplayName("throws when duplicate")
        void throwsOnDuplicate() {
            when(taskRepository.findTaskByStatement("dup"))
                    .thenReturn(Optional.of(new Task()));
            assertThatThrownBy(() -> testStrategy.validateStatement("dup"))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("validateOptionsSize")
    class ValidateOptionsSize {
        @ParameterizedTest
        @ValueSource(ints = {0, 1})
        @DisplayName("single-choice too few options")
        void tooFewSingle(int size) {
            List<Object> opts = Collections.nCopies(size, new Object());
            assertThatThrownBy(() -> testStrategy.validateOptionsSize(opts, Type.SINGLE_CHOICE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 2");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2})
        @DisplayName("multiple-choice too few options")
        void tooFewMulti(int size) {
            List<Object> opts = Collections.nCopies(size, new Object());
            assertThatThrownBy(() -> testStrategy.validateOptionsSize(opts, Type.MULTIPLE_CHOICE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 3");
        }

        @Test
        @DisplayName("rejects too many")
        void tooMany() {
            List<Object> opts = Collections.nCopies(6, new Object());
            assertThatThrownBy(() -> testStrategy.validateOptionsSize(opts, Type.SINGLE_CHOICE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between");
        }

        @Test
        @DisplayName("accepts valid")
        void valid() {
            testStrategy.validateOptionsSize(Collections.nCopies(2, new Object()), Type.SINGLE_CHOICE);
            testStrategy.validateOptionsSize(Collections.nCopies(3, new Object()), Type.MULTIPLE_CHOICE);
        }
    }


    @Nested
    @DisplayName("validateUniqueOptions")
    class ValidateUniqueOptions {
        @Test
        @DisplayName("throws on duplicates")
        void throwsOnDup() {
            List<Object> opts = List.of("a", "a");
            assertThatThrownBy(() -> testStrategy.validateUniqueOptions(opts))
                    .isInstanceOf(AlreadyExistsException.class);
        }

        @Test
        @DisplayName("accepts unique")
        void acceptsUnique() {
            testStrategy.validateUniqueOptions(List.of("a", "b", "a "));
        }
    }

    @Nested
    @DisplayName("countCorrect")
    class CountCorrect {
        @Test
        void counts() {
            ChoiceRequest r = Fixtures.choiceReq(1, 1,
                    Fixtures.opt("o1", true), Fixtures.opt("o2", false));
            assertThat(AbstractTaskStrategy.countCorrect(r)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getChoices")
    class GetChoices {
        @Test
        void mapsAndSetsTask() {
            ChoiceRequest r = Fixtures.choiceReq(1, 1,
                    Fixtures.opt("o1", true));
            Task saved = new Task();
            when(optionMapper.toEntity(any())).thenAnswer(i -> {
                Choice c = new Choice();
                c.setContent(((OptionDto) i.getArgument(0)).getOption());
                return c;
            });

            var list = testStrategy.getChoices(r, saved);
            assertThat(list).hasSize(1);
            Choice c = list.getFirst();
            assertThat(c.getContent()).isEqualTo("o1");
            assertThat(c.getTask()).isSameAs(saved);
        }
    }

    @Nested
    @DisplayName("validateCourseAndStatus")
    class ValidateCourseAndStatus {
        @Test
        @DisplayName("throws when non-building")
        void nonBuilding() {
            when(courseApi.getById(5L)).thenReturn(Fixtures.publishedCourse(5));
            assertThatThrownBy(() -> testStrategy.validateCourseAndStatus(5L, 1))
                    .isInstanceOf(GenericException.class)
                    .hasMessageContaining("BUILDING");
        }

        @Test
        @DisplayName("throws when order gap")
        void gapOrder() {
            when(courseApi.getById(1L)).thenReturn(Fixtures.buildingCourse(1));
            when(taskRepository.findAllByCourseIdOrderByTaskOrder(1L))
                    .thenReturn(List.of(Fixtures.t1(1), Fixtures.t1(2)));
            assertThatThrownBy(() -> testStrategy.validateCourseAndStatus(1L, 4))
                    .isInstanceOf(GenericException.class)
                    .hasMessageContaining("sequence");
        }

        @Test
        @DisplayName("reorders & saves existing")
        void reorders() {
            when(courseApi.getById(2L)).thenReturn(Fixtures.buildingCourse(2));
            Task one = Fixtures.t1(1), two = Fixtures.t1(2);
            when(taskRepository.findAllByCourseIdOrderByTaskOrder(2L))
                    .thenReturn(List.of(one, two));
            var cs = testStrategy.validateCourseAndStatus(2L, 2);

            // second becomes order=3, then new slot at 2
            assertThat(one.getTaskOrder()).isEqualTo(1);
            assertThat(two.getTaskOrder()).isEqualTo(3);
            then(taskRepository).should().saveAll(List.of(one, two));
            assertThat(cs.status()).isEqualTo(Status.BUILDING);
        }
    }

    @Nested
    @DisplayName("MultipleChoiceTaskStrategy – save(...)")
    class MCStrategyTest {
        @InjectMocks
        MultipleChoiceTaskStrategy svc;
        @Captor
        ArgumentCaptor<Task> taskCaptor;

        @BeforeEach
        void init() {
            svc = new MultipleChoiceTaskStrategy(taskRepository, optionMapper, courseApi, requestMapper);
        }

        @Test
        @DisplayName("happy path – exactly two correct out of ≥3")
        void shouldSaveWhenValid() {
            // three options, two correct
            ChoiceRequest dto = Fixtures.choiceReq(1, 1,
                    Fixtures.opt("A", true),
                    Fixtures.opt("B", true),
                    Fixtures.opt("C", false)
            );

            when(taskRepository.findTaskByStatement("stmt")).thenReturn(Optional.empty());
            when(courseApi.getById(1L)).thenReturn(Fixtures.buildingCourse(1L));
            when(taskRepository.findAllByCourseIdOrderByTaskOrder(1L)).thenReturn(List.of());
            Task mapped = new Task();
            when(requestMapper.toMultipleChoiceEntity(dto)).thenReturn(mapped);
            when(taskRepository.save(mapped)).thenReturn(mapped);
            when(optionMapper.toEntity(any())).thenReturn(new Choice());

            svc.save(dto);

            // should save twice: initial Task and then Task with choices
            verify(taskRepository, times(2)).save(taskCaptor.capture());
            Task first = taskCaptor.getAllValues().get(0);
            Task second = taskCaptor.getAllValues().get(1);
            assertThat(first).isSameAs(mapped);
            assertThat(second.getChoices()).hasSize(3);
        }

        @Test
        @DisplayName("rejects if fewer than two correct (size ≥3)")
        void rejectsTooFewCorrect() {
            // still three options, but only 1 correct
            ChoiceRequest dto = Fixtures.choiceReq(1, 1,
                    Fixtures.opt("A", true),
                    Fixtures.opt("B", false),
                    Fixtures.opt("C", false)
            );
            when(taskRepository.findTaskByStatement("stmt")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> svc.save(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("two correct");
        }
    }

    // -----------------------------------------------------
    // OpenTextTaskStrategy
    // -----------------------------------------------------

    @Nested
    @DisplayName("OpenTextTaskStrategy")
    class OTStrategyTest {
        @InjectMocks
        OpenTextTaskStrategy svc;

        @BeforeEach
        void init() {
            svc = new OpenTextTaskStrategy(taskRepository, optionMapper, courseApi, requestMapper);
        }

        @Test
        @DisplayName("saves when valid")
        void saves() {
            var dto = Fixtures.openReq(1, 1);
            when(taskRepository.findTaskByStatement("stmt")).thenReturn(Optional.empty());
            when(courseApi.getById(1L)).thenReturn(Fixtures.buildingCourse(1));
            when(taskRepository.findAllByCourseIdOrderByTaskOrder(1L)).thenReturn(List.of());
            Task mapped = new Task();
            when(requestMapper.toOpenTextEntity(dto)).thenReturn(mapped);

            svc.save(dto);

            verify(taskRepository).save(mapped);
        }

        @Test
        @DisplayName("rejects duplicate")
        void rejectsDup() {
            when(taskRepository.findTaskByStatement("stmt"))
                    .thenReturn(Optional.of(new Task()));
            assertThatThrownBy(() -> svc.save(Fixtures.openReq(1, 1)))
                    .isInstanceOf(AlreadyExistsException.class);
        }
    }

    // -----------------------------------------------------
    // SingleChoiceTaskStrategy
    // -----------------------------------------------------

    @Nested
    @DisplayName("SingleChoiceTaskStrategy")
    class SCStrategyTest {
        @InjectMocks
        SingleChoiceTaskStrategy svc;

        @BeforeEach
        void init() {
            svc = new SingleChoiceTaskStrategy(taskRepository, optionMapper, courseApi, requestMapper);
        }

        @Test
        @DisplayName("saves when exactly 1 correct")
        void saves() {
            var dto = Fixtures.choiceReq(1, 1,
                    Fixtures.opt("A", true), Fixtures.opt("B", false));
            when(taskRepository.findTaskByStatement("stmt")).thenReturn(Optional.empty());
            when(courseApi.getById(1L)).thenReturn(Fixtures.buildingCourse(1));
            when(taskRepository.findAllByCourseIdOrderByTaskOrder(1L)).thenReturn(List.of());
            Task mapped = new Task();
            when(requestMapper.toSingleChoiceEntity(dto)).thenReturn(mapped);
            when(taskRepository.save(mapped)).thenReturn(mapped);
            when(optionMapper.toEntity(any())).thenReturn(new Choice());

            svc.save(dto);

            verify(taskRepository, times(2)).save(any());
        }

        @Test
        @DisplayName("rejects wrong correct count")
        void rejectsWrongCount() {
            var dto = Fixtures.choiceReq(1, 1,
                    Fixtures.opt("A", false), Fixtures.opt("B", false));
            when(taskRepository.findTaskByStatement("stmt")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> svc.save(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("exactly 1");
        }
    }
}
