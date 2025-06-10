package br.com.marques.byteclass.feature.course.adapter.controller;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.course.domain.Status;
import br.com.marques.byteclass.feature.course.port.CoursePort;
import br.com.marques.byteclass.feature.course.port.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.util.CourseTestUtils.*;
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
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    CoursePort coursePort;
    @Autowired
    ObjectMapper mapper;

    static class Fixtures {
        static CourseRequestBuilder validReq() {
            return new CourseRequestBuilder();
        }

        static CourseSummary summary(Long id, String title) {
            return new CourseSummary(id, title, "Desc", Status.BUILDING);
        }
    }

    @Nested
    @DisplayName("POST /api/course")
    class CreateCourse {

        @Test
        @DisplayName("201 → retorna id gerado")
        void should_create_and_return_id() throws Exception {
            CourseRequest req = Fixtures.validReq().build();
            when(coursePort.create(any(CourseRequest.class))).thenReturn(42L);

            mvc.perform(post("/api/course")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("42"));
        }

        @ParameterizedTest(name = "#{index} → payload inválido gera 400")
        @MethodSource("invalidRequests")
        void should_reject_invalid_payload(CourseRequest bad) throws Exception {
            mvc.perform(post("/api/course")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        static Stream<CourseRequest> invalidRequests() {
            return Stream.of(
                    new CourseRequest("", ""),                      // ambos vazios
                    new CourseRequest(null, "desc"),                // título nulo
                    new CourseRequest("Java", null)                 // descrição nula
            );
        }
    }

    @Nested
    @DisplayName("GET /api/course")
    class ListCourses {

        @Test
        @DisplayName("200 → retorna página de cursos")
        void should_return_page() throws Exception {
            CourseSummary a = Fixtures.summary(1L, "Java");
            CourseSummary b = Fixtures.summary(2L, "Spring");
            Page<CourseSummary> page = new PageImpl<>(List.of(a, b),
                    PageRequest.of(0, 2, Sort.by("title")), 2);
            when(coursePort.list(any(Pageable.class))).thenReturn(page);

            mvc.perform(get("/api/course")
                            .param("page", "0")
                            .param("size", "2")
                            .param("sort", "title,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].title").value("Java"))
                    .andExpect(jsonPath("$.content[1].title").value("Spring"));
        }
    }

    @Nested
    @DisplayName("GET /api/course/{id}")
    class GetCourse {

        @Test
        @DisplayName("200 → retorna curso existente")
        void should_return_course() throws Exception {
            when(coursePort.getById(1L)).thenReturn(Fixtures.summary(1L, "Java"));

            mvc.perform(get("/api/course/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Java"));
        }

        @ParameterizedTest(name = "404 quando id={0} não encontrado")
        @ValueSource(longs = {99L, 100L})
        void should_return_404_when_not_found(long id) throws Exception {
            when(coursePort.getById(id)).thenThrow(new NotFoundException("Course not found"));

            mvc.perform(get("/api/course/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Course not found"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/course/{id}")
    class UpdateCourse {

        @Test
        @DisplayName("200 → atualização bem-sucedida")
        void should_update() throws Exception {
            doNothing().when(coursePort).update(eq(1L), any(CourseRequest.class));

            mvc.perform(patch("/api/course/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(
                                    Fixtures.validReq().title("Updated").build())))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/course/{id}")
    class DeleteCourse {

        @Test
        @DisplayName("200 → remoção bem-sucedida")
        void should_delete() throws Exception {
            doNothing().when(coursePort).delete(1L);

            mvc.perform(delete("/api/course/1"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /api/course/{id}/publish")
    class PublishCourse {

        @Test
        @DisplayName("200 → publicação bem-sucedida")
        void should_publish() throws Exception {
            doNothing().when(coursePort).publish(1L);

            mvc.perform(post("/api/course/1/publish"))
                    .andExpect(status().isOk());
        }
    }
}
