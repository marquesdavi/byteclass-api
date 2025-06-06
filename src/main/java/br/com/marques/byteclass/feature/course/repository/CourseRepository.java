package br.com.marques.byteclass.feature.course.repository;

import br.com.marques.byteclass.feature.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long>{

}
