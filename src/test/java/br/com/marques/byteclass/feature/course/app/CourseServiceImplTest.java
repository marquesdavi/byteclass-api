package br.com.marques.byteclass.feature.course.app;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.common.util.PageableRequest;
import br.com.marques.byteclass.feature.course.adapter.repository.CourseRepository;
import br.com.marques.byteclass.feature.course.app.facade.CoursePublishingFacade;
import br.com.marques.byteclass.feature.course.domain.Course;
import br.com.marques.byteclass.feature.course.domain.Status;
import br.com.marques.byteclass.feature.course.port.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.course.port.mapper.CourseRequestMapper;
import br.com.marques.byteclass.feature.course.port.mapper.CourseResponseMapper;
import br.com.marques.byteclass.feature.user.domain.Role;
import br.com.marques.byteclass.feature.user.port.AuthenticationPort;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import br.com.marques.byteclass.feature.util.CourseTestUtils.CourseRequestBuilder;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @InjectMocks
    CourseServiceImpl service;

    @Mock
    CourseRepository courseRepository;

    @Mock
    AuthenticationPort authApi;

    @Mock
    CourseRequestMapper requestMapper;

    @Mock
    CourseResponseMapper summaryMapper;

    @Mock
    CoursePublishingFacade publishingFacade;

    @Captor
    ArgumentCaptor<Course> courseCaptor;

    static class Fixtures {

        static CourseRequestBuilder validReq() { return new CourseRequestBuilder(); }

        static UserSummary user(Long id) { return new UserSummary(id, "Name", "email@domain", Role.INSTRUCTOR); }

        static Course domainCourse(Long id) {
            Course c = new Course();
            c.setId(id);
            c.setTitle("Title");
            c.setDescription("Desc");
            c.setInstructorId(1L);
            c.setStatus(Status.BUILDING);
            return c;
        }

        static CourseSummary summary(Long id) {
            return new CourseSummary(id, "Title", "Desc", Status.BUILDING);
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should map, save and return generated id")
        void should_create_and_return_id() {
            CourseRequest req = Fixtures.validReq().build();
            Course domain = Fixtures.domainCourse(null);
            when(authApi.getAuthenticated()).thenReturn(Fixtures.user(99L));
            when(requestMapper.toEntity(req)).thenReturn(domain);
            when(courseRepository.save(domain)).thenAnswer(inv -> {
                domain.setId(42L);
                return domain;
            });

            Long result = service.create(req);

            assertThat(result).isEqualTo(42L);
            verify(requestMapper).toEntity(req);
            verify(courseRepository).save(domain);
        }
    }

    @Nested
    @DisplayName("list()")
    class ListAll {

        @Test
        @DisplayName("should return paged summaries")
        void should_return_page() {
            Course c1 = Fixtures.domainCourse(1L);
            Course c2 = Fixtures.domainCourse(2L);
            PageableRequest req = new PageableRequest(0, 2, "ASC", "title");
            var pageable = req.toPageable();
            Page<Course> pageDomain = new PageImpl<>(List.of(c1, c2), pageable, 2);
            CourseSummary s1 = Fixtures.summary(1L);
            CourseSummary s2 = Fixtures.summary(2L);

            when(courseRepository.findAll(pageable))
                .thenReturn(pageDomain);
            when(summaryMapper.toDto(c1)).thenReturn(s1);
            when(summaryMapper.toDto(c2)).thenReturn(s2);

            Page<CourseSummary> page = service.list(req);

            assertThat(page.getContent()).containsExactly(s1, s2);
            verify(courseRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("should return summary when found")
        void should_return_summary() {
            Course domain = Fixtures.domainCourse(5L);
            CourseSummary summary = Fixtures.summary(5L);

            when(courseRepository.findById(5L)).thenReturn(Optional.of(domain));
            when(summaryMapper.toDto(domain)).thenReturn(summary);

            CourseSummary result = service.getById(5L);

            assertThat(result).isEqualTo(summary);
        }

        @ParameterizedTest(name = "should throw when id={0} not found")
        @ValueSource(longs = {0, -1, 99})
        void should_throw_not_found(long id) {
            when(courseRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(id))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Course not found");
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("should update existing course")
        void should_update() {
            Course domain = Fixtures.domainCourse(7L);
            when(courseRepository.findById(7L)).thenReturn(Optional.of(domain));
            CourseRequest req = Fixtures.validReq().title("X").description("Y").build();

            service.update(7L, req);

            verify(courseRepository).save(courseCaptor.capture());
            Course saved = courseCaptor.getValue();
            assertThat(saved.getTitle()).isEqualTo("X");
            assertThat(saved.getDescription()).isEqualTo("Y");
        }

        @Test
        @DisplayName("should throw when not found")
        void should_throw_not_found() {
            when(courseRepository.findById(8L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.update(8L, Fixtures.validReq().build()))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete existing course")
        void should_delete() {
            Course domain = Fixtures.domainCourse(9L);
            when(courseRepository.findById(9L)).thenReturn(Optional.of(domain));

            service.delete(9L);

            verify(courseRepository).delete(domain);
        }

        @Test
        @DisplayName("should throw when not found")
        void should_throw_not_found() {
            when(courseRepository.findById(10L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.delete(10L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("publish()")
    class Publish {

        @Test
        @DisplayName("should delegate to facade")
        void should_delegate_to_facade() {
            doNothing().when(publishingFacade).publishCourse(11L);

            service.publish(11L);

            verify(publishingFacade).publishCourse(11L);
        }

        @Test
        @DisplayName("should propagate exception from facade")
        void should_propagate_exception() {
            doThrow(new IllegalStateException("fail"))
                    .when(publishingFacade).publishCourse(12L);

            assertThatThrownBy(() -> service.publish(12L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("fail");
        }
    }
}
