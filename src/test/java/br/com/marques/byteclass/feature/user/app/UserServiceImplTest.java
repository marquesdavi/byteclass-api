package br.com.marques.byteclass.feature.user.app;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.user.adapter.repository.UserRepository;
import br.com.marques.byteclass.feature.user.domain.Role;
import br.com.marques.byteclass.feature.user.domain.User;
import br.com.marques.byteclass.feature.user.port.dto.UserDetailsInternal;
import br.com.marques.byteclass.feature.user.port.dto.UserRequest;
import br.com.marques.byteclass.feature.user.port.dto.UserSummary;
import br.com.marques.byteclass.feature.user.port.mapper.UserRequestMapper;
import br.com.marques.byteclass.feature.user.port.mapper.UserResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder encoder;
    @Mock
    UserRequestMapper requestMapper;
    @Mock
    UserResponseMapper summaryMapper;
    @InjectMocks
    UserServiceImpl service;

    static class Fixtures {
        static UserRequest defaultRequest() {
            return new UserRequest("foo@domain.com", "Foo User", "plainPass");
        }

        static UserRequest otherRequest() {
            return new UserRequest("bar@domain.com", "Bar User", "otherPass");
        }

        static User entity() {
            User u = new User();
            u.setId(42L);
            u.setEmail("foo@domain.com");
            u.setName("Foo User");
            u.setRole(Role.STUDENT);
            return u;
        }

        static UserSummary summary(User u) {
            return new UserSummary(u.getId(), u.getName(), u.getEmail(), u.getRole());
        }

        static UserDetailsInternal internal(User u) {
            return new UserDetailsInternal(u.getId(), u.getEmail(), u.getName(), u.getPassword(), u.getRole());
        }
    }

    @BeforeEach
    void setUp() {
    }

    @Nested
    @DisplayName("create(dto)")
    class CreateTests {
        @Test
        @DisplayName("should create when email does not exist")
        void shouldCreate() {
            var dto = Fixtures.defaultRequest();
            var user = Fixtures.entity();

            when(userRepository.existsByEmail(dto.email())).thenReturn(false);
            when(requestMapper.toEntity(dto)).thenReturn(user);
            when(encoder.encode(dto.password())).thenReturn("encrypted");

            Long id = service.create(dto);

            assertThat(id).isEqualTo(42L);
            assertThat(user.getPassword()).isEqualTo("encrypted");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should fail when email already exists")
        void shouldFailWhenExists() {
            var dto = Fixtures.defaultRequest();
            when(userRepository.existsByEmail(dto.email())).thenReturn(true);

            assertThatThrownBy(() -> service.create(dto))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessageContaining(dto.email());

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update(id, dto)")
    class UpdateTests {
        @Test
        @DisplayName("should update when everything is valid")
        void shouldUpdate() {
            var existing = Fixtures.entity();
            var dto = Fixtures.otherRequest();

            when(userRepository.findById(42L)).thenReturn(Optional.of(existing));
            when(userRepository.existsByEmail(dto.email())).thenReturn(false);
            doNothing().when(requestMapper).updateEntityFromDto(dto, existing);
            when(encoder.encode(dto.password())).thenReturn("crypted");

            service.update(42L, dto);

            verify(requestMapper).updateEntityFromDto(dto, existing);
            assertThat(existing.getPassword()).isEqualTo("crypted");
            verify(userRepository).save(existing);
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 99L})
        @DisplayName("should throw NotFoundException if id does not exist")
        void shouldThrowNotFoundOnMissingId(Long id) {
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(id, Fixtures.defaultRequest()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("should fail on email conflict")
        void shouldFailOnEmailConflict() {
            var existing = Fixtures.entity();
            existing.setEmail("old@domain.com");
            var dto = Fixtures.defaultRequest();

            when(userRepository.findById(42L)).thenReturn(Optional.of(existing));
            when(userRepository.existsByEmail(dto.email())).thenReturn(true);

            assertThatThrownBy(() -> service.update(42L, dto))
                    .isInstanceOf(AlreadyExistsException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getById(id)")
    class GetByIdTests {
        @Test
        @DisplayName("should return summary when found")
        void shouldReturnSummary() {
            var existing = Fixtures.entity();
            var summary = Fixtures.summary(existing);

            when(userRepository.findById(42L)).thenReturn(Optional.of(existing));
            when(summaryMapper.toDto(existing)).thenReturn(summary);

            UserSummary result = service.getById(42L);
            assertThat(result).isEqualTo(summary);
        }

        @ParameterizedTest
        @ValueSource(longs = {5L, 10L})
        @DisplayName("should throw NotFoundException when not found")
        void shouldThrowNotFound(Long id) {
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(id))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("list(pageable)")
    class ListTests {
        @Test
        @DisplayName("should return a page of summaries")
        void shouldList() {
            var existing = Fixtures.entity();
            var pageable = PageRequest.of(0, 10, Sort.by("name"));
            var pageEnt = new PageImpl<>(List.of(existing), pageable, 1);
            var summary = Fixtures.summary(existing);

            when(userRepository.findAll(pageable)).thenReturn(pageEnt);
            when(summaryMapper.toDto(existing)).thenReturn(summary);

            Page<UserSummary> result = service.list(pageable);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).containsExactly(summary);
        }
    }

    @Nested
    @DisplayName("delete(id)")
    class DeleteTests {
        @Test
        @DisplayName("should delete when found")
        void shouldDelete() {
            var existing = Fixtures.entity();
            when(userRepository.findById(42L)).thenReturn(Optional.of(existing));

            service.delete(42L);
            verify(userRepository).delete(existing);
        }

        @ParameterizedTest
        @ValueSource(longs = {7L, 8L})
        @DisplayName("should throw NotFoundException when not found")
        void shouldThrowNotFound(Long id) {
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByEmail(email)")
    class FindByEmailTests {
        @Test
        @DisplayName("should return internal details when found")
        void shouldReturnInternal() {
            var existing = Fixtures.entity();
            var internal = Fixtures.internal(existing);

            when(userRepository.findByEmail("foo@domain.com"))
                    .thenReturn(Optional.of(existing));
            when(summaryMapper.toInternalDto(existing)).thenReturn(internal);

            UserDetailsInternal result = service.findByEmail("foo@domain.com");
            assertThat(result).isEqualTo(internal);
        }

        @ParameterizedTest
        @ValueSource(strings = {"absent@mail", "unknown@domain.com"})
        @DisplayName("should throw NotFoundException when not found")
        void shouldThrowNotFound(String email) {
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findByEmail(email))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(email);
        }
    }
}
