package br.com.marques.byteclass.common.util;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.course.repository.CourseRepository;
import br.com.marques.byteclass.feature.user.entity.Role;
import br.com.marques.byteclass.feature.user.repository.UserRepository;
import br.com.marques.byteclass.feature.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (!"dev".equals(activeProfile)) return;

        if (userRepository.count() == 0) {
            User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT);
            User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
            userRepository.saveAll(Arrays.asList(caio, paulo));
            courseRepository.save(new Course("Java", "Aprenda Java com Alura", paulo));
        }
    }
}