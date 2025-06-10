package br.com.marques.byteclass.feature.user.adapter.controller;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.common.util.PageableRequest;
import br.com.marques.byteclass.feature.user.domain.Role;
import br.com.marques.byteclass.feature.user.port.UserPort;
import br.com.marques.byteclass.feature.user.port.dto.UserRequest;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import br.com.marques.byteclass.feature.util.UserTestUtils.*;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    UserPort userPort;
    @Autowired
    ObjectMapper mapper;

    static class Fixtures {
        static UserRequestBuilder validReq() {
            return new UserRequestBuilder();
        }

        static UserSummary summary(Long id) {
            return new UserSummary(id,
                    "User" + id,
                    "user" + id + "@example.com",
                    Role.STUDENT);
        }
    }

    @Nested
    @DisplayName("POST /api/user")
    class CreateUser {

        @Test
        @DisplayName("201 → returns generated id")
        void should_create_and_return_id() throws Exception {
            UserRequest req = Fixtures.validReq().build();
            when(userPort.create(any(UserRequest.class))).thenReturn(123L);

            mvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("123"));
        }

        @ParameterizedTest(name = "#{index} → invalid payload yields 400")
        @MethodSource("invalidRequests")
        void should_reject_invalid_payload(UserRequest bad) throws Exception {
            mvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bad)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray());
        }

        static Stream<UserRequest> invalidRequests() {
            return Stream.of(
                    new UserRequest("", "", ""),                        // todos vazios
                    new UserRequest(null, "a@b.com", "pass"),           // nome nulo
                    new UserRequest("Name", null, "pass"),              // email nulo
                    new UserRequest("Name", "no-email", "pass"),        // email inválido
                    new UserRequest("Name", "a@b.com", "")              // senha vazia
            );
        }
    }

    @Nested
    @DisplayName("GET /api/user")
    class ListUsers {

        @Test
        @DisplayName("200 → returns a page of users")
        void should_return_page() throws Exception {
            UserSummary u1 = Fixtures.summary(1L);
            UserSummary u2 = Fixtures.summary(2L);
            Page<UserSummary> page = new PageImpl<>(List.of(u1, u2),
                PageRequest.of(0,2, Sort.by("name")), 2);

            when(userPort.list(any(PageableRequest.class))).thenReturn(page);

            mvc.perform(get("/api/user")
                    .param("page", "0")
                    .param("size", "2")
                    .param("direction", "ASC")
                    .param("orderBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value(u1.name()))
                .andExpect(jsonPath("$.content[1].name").value(u2.name()));
        }
    }

    @Nested
    @DisplayName("GET /api/user/{id}")
    class GetUser {

        @Test
        @DisplayName("200 → returns existing user")
        void should_return_user() throws Exception {
            UserSummary summary = Fixtures.summary(5L);
            when(userPort.getById(5L)).thenReturn(summary);

            mvc.perform(get("/api/user/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(summary.name()))
                    .andExpect(jsonPath("$.email").value(summary.email()));
        }

        @ParameterizedTest(name = "404 when id={0} not found")
        @ValueSource(longs = {99L, 100L})
        void should_return_404_when_not_found(long id) throws Exception {
            when(userPort.getById(id))
                    .thenThrow(new NotFoundException("User not found"));

            mvc.perform(get("/api/user/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/user/{id}")
    class UpdateUser {

        @Test
        @DisplayName("200 → successful update")
        void should_update() throws Exception {
            doNothing().when(userPort).update(eq(7L), any(UserRequest.class));

            mvc.perform(patch("/api/user/7")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(
                                    Fixtures.validReq().name("New Name").build())))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/user/{id}")
    class DeleteUser {

        @Test
        @DisplayName("200 → successful deletion")
        void should_delete() throws Exception {
            doNothing().when(userPort).delete(8L);

            mvc.perform(delete("/api/user/8"))
                    .andExpect(status().isOk());
        }
    }
}
