package br.com.marques.byteclass.feature.task.adapter.repository;

import br.com.marques.byteclass.feature.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findTaskByStatement(String statement);

    @Query("SELECT t FROM Task t WHERE t.courseId = :courseId ORDER BY t.taskOrder")
    List<Task> findAllByCourseIdOrderByTaskOrder(Long courseId);

}

