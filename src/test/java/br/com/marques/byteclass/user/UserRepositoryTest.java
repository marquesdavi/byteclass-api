package br.com.marques.byteclass.user;

import br.com.marques.byteclass.feature.user.adapter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

//    @Test
//    void findByEmail__should_return_existis_user() {
//        User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT);
//        userRepository.save(caio);
//
//        Optional<User> result = userRepository.findByEmail("caio@alura.com.br");
//        assertThat(result).isPresent();
//        assertThat(result.get().getName()).isEqualTo("Caio");
//
//        result = userRepository.findByEmail("sergio@alura.com.br");
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void existsByEmail__should_return_true_when_user_existis() {
//        User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT);
//        userRepository.save(caio);
//
//        assertThat(userRepository.existsByEmail("caio@alura.com.br")).isTrue();
//        assertThat(userRepository.existsByEmail("sergio@alura.com.br")).isFalse();
//    }

}