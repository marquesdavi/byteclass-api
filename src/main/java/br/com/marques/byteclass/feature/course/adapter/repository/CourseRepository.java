package br.com.marques.byteclass.feature.course.adapter.repository;

import br.com.marques.byteclass.feature.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

}
