package br.com.marques.byteclass.feature.task.adapter.controller;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.task.domain.Choice;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.port.TaskPort;
import br.com.marques.byteclass.feature.task.port.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    TaskPort taskPort;
    @Autowired
    ObjectMapper mapper;

    static class Fixtures {
        static OpenTextRequest validOpen() {
            OpenTextRequest r = new OpenTextRequest();
            r.setCourseId(1L);
            r.setStatement("Valid statement");
            r.setOrder(1);
            return r;
        }

        static ChoiceRequest validChoice() {
            ChoiceRequest r = new ChoiceRequest();
            r.setCourseId(2L);
            r.setStatement("Valid choice statement");
            r.setOrder(2);
            OptionDto o1 = new OptionDto();
            o1.setOption("Option A");
            o1.setIsCorrect(false);
            OptionDto o2 = new OptionDto();
            o2.setOption("Option B");
            o2.setIsCorrect(true);
            r.setOptions(List.of(o1, o2));
            return r;
        }

        static TaskSummary summary(Long courseId, String stmt, Integer order, Type type) {
            return new TaskSummary(courseId, stmt, order, type);
        }

        static Choice domainChoice(Long id, String text, Boolean isCorrect) {
            return Choice.builder()
                    .id(id)
                    .content(text)
                    .isCorrect(isCorrect)
                    .task(null)
                    .build();
        }

        static TaskDetails details(Long id,
                                   Long courseId,
                                   String stmt,
                                   Integer order,
                                   Type type,
                                   List<Choice> choices) {
            return new TaskDetails(id, courseId, stmt, order, type, choices);
        }
    }

    @Nested
    @DisplayName("POST /api/task/new/opentext")
    class CreateOpenText {
        @Test
        @DisplayName("201 → creates open-text task successfully")
        void shouldCreateOpenText() throws Exception {
            OpenTextRequest req = Fixtures.validOpen();
            doNothing().when(taskPort).createOpenText(any(OpenTextRequest.class));

            mvc.perform(post("/api/task/new/opentext")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest(name = "invalid payload #{index}")
        @MethodSource("invalidRequests")
        @DisplayName("400 → invalid open-text payload")
        void shouldRejectInvalid(OpenTextRequest bad) throws Exception {
            mvc.perform(post("/api/task/new/opentext")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        static Stream<OpenTextRequest> invalidRequests() {
            OpenTextRequest r1 = new OpenTextRequest();
            r1.setCourseId(null);
            r1.setStatement("Valid statement");
            r1.setOrder(1);

            OpenTextRequest r2 = new OpenTextRequest();
            r2.setCourseId(1L);
            r2.setStatement("No");
            r2.setOrder(1);

            OpenTextRequest r3 = new OpenTextRequest();
            r3.setCourseId(1L);
            r3.setStatement("Valid statement");
            r3.setOrder(0);

            return Stream.of(r1, r2, r3);
        }
    }

    @Nested
    @DisplayName("POST /api/task/new/singlechoice")
    class CreateSingleChoice {
        @Test
        @DisplayName("201 → creates single-choice task successfully")
        void shouldCreateSingle() throws Exception {
            ChoiceRequest req = Fixtures.validChoice();
            doNothing().when(taskPort).createSingleChoice(any(ChoiceRequest.class));

            mvc.perform(post("/api/task/new/singlechoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest(name = "invalid payload #{index}")
        @MethodSource("invalidRequests")
        @DisplayName("400 → invalid single-choice payload")
        void shouldRejectInvalid(ChoiceRequest bad) throws Exception {
            mvc.perform(post("/api/task/new/singlechoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        static Stream<ChoiceRequest> invalidRequests() {
            // 1) null courseId
            ChoiceRequest r1 = new ChoiceRequest();
            r1.setCourseId(null);
            r1.setStatement("Valid");
            r1.setOrder(1);
            r1.setOptions(List.of(new OptionDto(), new OptionDto()));

            // 2) statement too short
            ChoiceRequest r2 = new ChoiceRequest();
            r2.setCourseId(1L);
            r2.setStatement("No");
            r2.setOrder(1);
            OptionDto a2 = new OptionDto();
            a2.setOption("Opt1");
            a2.setIsCorrect(true);
            OptionDto b2 = new OptionDto();
            b2.setOption("Opt2");
            b2.setIsCorrect(false);
            r2.setOptions(List.of(a2, b2));

            // 3) order invalid
            ChoiceRequest r3 = new ChoiceRequest();
            r3.setCourseId(1L);
            r3.setStatement("Valid statement");
            r3.setOrder(0);
            r3.setOptions(List.of(a2, b2));

            // 4) null options
            ChoiceRequest r4 = new ChoiceRequest();
            r4.setCourseId(1L);
            r4.setStatement("Valid statement");
            r4.setOrder(1);
            r4.setOptions(null);

            // 5) only one option
            ChoiceRequest r5 = new ChoiceRequest();
            r5.setCourseId(1L);
            r5.setStatement("Valid statement");
            r5.setOrder(1);
            OptionDto single = new OptionDto();
            single.setOption("Option");
            single.setIsCorrect(true);
            r5.setOptions(List.of(single));

            return Stream.of(r1, r2, r3, r4, r5);
        }
    }

    @Nested
    @DisplayName("POST /api/task/new/multiplechoice")
    class CreateMultipleChoice {
        @Test
        @DisplayName("201 → creates multiple-choice task successfully")
        void shouldCreateMultiple() throws Exception {
            ChoiceRequest req = Fixtures.validChoice();
            doNothing().when(taskPort).createMultipleChoice(any(ChoiceRequest.class));

            mvc.perform(post("/api/task/new/multiplechoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest(name = "invalid payload #{index}")
        @MethodSource("invalidRequests")
        @DisplayName("400 → invalid multiple-choice payload")
        void shouldRejectInvalid(ChoiceRequest bad) throws Exception {
            mvc.perform(post("/api/task/new/multiplechoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        static Stream<ChoiceRequest> invalidRequests() {
            return CreateSingleChoice.invalidRequests();
        }
    }

    @Nested
    @DisplayName("GET /api/task/course/{id}")
    class ListByCourse {
        @Test
        @DisplayName("200 → returns list of summaries")
        void shouldListByCourse() throws Exception {
            TaskSummary a = Fixtures.summary(1L, "Stmt A", 1, Type.OPEN_TEXT);
            TaskSummary b = Fixtures.summary(1L, "Stmt B", 2, Type.SINGLE_CHOICE);
            when(taskPort.listByCourseId(1L)).thenReturn(List.of(a, b));

            mvc.perform(get("/api/task/course/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].statement").value("Stmt A"))
                    .andExpect(jsonPath("$[1].taskType").value("SINGLE_CHOICE"));
        }
    }

    @Nested
    @DisplayName("GET /api/task/{id}")
    class GetById {
        @Test
        @DisplayName("200 → returns task details")
        void shouldReturnDetails() throws Exception {
            var choices = List.of(
                    Fixtures.domainChoice(10L, "Opt1", true),
                    Fixtures.domainChoice(11L, "Opt2", false)
            );
            TaskDetails det = Fixtures.details(5L, 1L, "Detail stmt", 3, Type.MULTIPLE_CHOICE, choices);
            when(taskPort.getById(5L)).thenReturn(det);

            mvc.perform(get("/api/task/{id}", 5L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.courseId").value(1))
                    .andExpect(jsonPath("$.statement").value("Detail stmt"))
                    .andExpect(jsonPath("$.taskOrder").value(3))
                    .andExpect(jsonPath("$.taskType").value("MULTIPLE_CHOICE"))
                    .andExpect(jsonPath("$.choices.length()").value(2));
        }

        @ParameterizedTest(name = "404 when id={0} not found")
        @ValueSource(longs = {99L, 100L})
        @DisplayName("404 → NotFoundException for missing task")
        void shouldReturn404(Long id) throws Exception {
            when(taskPort.getById(id)).thenThrow(new NotFoundException("Task not found"));

            mvc.perform(get("/api/task/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }
    }
}
