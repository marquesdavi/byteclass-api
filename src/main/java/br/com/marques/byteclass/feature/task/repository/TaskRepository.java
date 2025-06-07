package br.com.marques.byteclass.feature.task.repository;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findTaskByStatement(String statement);

    List<Task> findAllByCourseIdOrderByTaskOrder(Course courseId);
}

