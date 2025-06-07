package br.com.marques.byteclass.feature.task.service.strategy;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.course.repository.CourseRepository;
import br.com.marques.byteclass.feature.task.api.dto.OpenTextRequest;
import br.com.marques.byteclass.feature.task.api.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.entity.Task;
import br.com.marques.byteclass.feature.task.entity.Type;
import br.com.marques.byteclass.feature.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class OpenTextTaskStrategy extends AbstractTaskStrategy {
    public OpenTextTaskStrategy(TaskRepository tr, CourseRepository cr) {
        super(tr, cr);
    }

    @Override
    public Type getType() {
        return Type.OPEN_TEXT;
    }

    @Override
    public void save(TaskRequest dto) {
        OpenTextRequest open = (OpenTextRequest) dto;
        validateStatement(open.getStatement());
        Course course = validateCourseAndStatus(open.getCourseId(), open.getOrder());
        Task task = open.toEntity(course, Type.OPEN_TEXT);
        taskRepository.save(task);
    }
}
