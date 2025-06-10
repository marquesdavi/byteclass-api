package br.com.marques.byteclass.feature.user.adapter.repository;

import br.com.marques.byteclass.feature.user.domain.Role;
import br.com.marques.byteclass.feature.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    static class Fixtures {
        static User user(String email) {
            User u = new User();
            u.setName("Test User");
            u.setEmail(email);
            u.setPassword("secret");
            u.setRole(Role.STUDENT);
            return u;
        }
    }

    @Nested
    @DisplayName("findByEmail(String)")
    class FindByEmail {

        @Test
        @DisplayName("should return user when email exists")
        void should_return_user_if_email_exists() {
            User saved = userRepository.save(Fixtures.user("foo@domain.com"));

            Optional<User> result = userRepository.findByEmail("foo@domain.com");

            assertThat(result)
                    .isPresent()
                    .get()
                    .satisfies(u -> {
                        assertThat(u.getId()).isEqualTo(saved.getId());
                        assertThat(u.getEmail()).isEqualTo("foo@domain.com");
                    });
        }

        @Test
        @DisplayName("should return empty when email does not exist")
        void should_return_empty_if_email_not_found() {
            Optional<User> result = userRepository.findByEmail("missing@domain.com");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail(String)")
    class ExistsByEmail {

        @Test
        @DisplayName("should return true when email exists")
        void should_return_true_if_email_exists() {
            userRepository.save(Fixtures.user("bar@domain.com"));

            boolean exists = userRepository.existsByEmail("bar@domain.com");
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("should return false when email does not exist")
        void should_return_false_if_email_not_found() {
            boolean exists = userRepository.existsByEmail("unknown@domain.com");
            assertThat(exists).isFalse();
        }
    }
}
