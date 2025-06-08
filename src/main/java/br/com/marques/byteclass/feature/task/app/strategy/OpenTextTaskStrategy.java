package br.com.marques.byteclass.feature.task.app.strategy;

import br.com.marques.byteclass.feature.course.port.CoursePort;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.task.port.dto.OpenTextRequest;
import br.com.marques.byteclass.feature.task.port.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.port.mapper.OptionMapper;
import br.com.marques.byteclass.feature.task.port.mapper.TaskRequestMapper;
import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.adapter.repository.TaskRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpenTextTaskStrategy extends AbstractTaskStrategy {
    private final TaskRequestMapper mapper;

    public OpenTextTaskStrategy(TaskRepository taskRepository,
                                OptionMapper optionMapper,
                                @Lazy CoursePort coursePort,
                                TaskRequestMapper mapper) {
        super(taskRepository, optionMapper, coursePort);
        this.mapper = mapper;
    }

    @Override
    public Type getType() {
        return Type.OPEN_TEXT;
    }

    @Override
    @Transactional
    public void save(TaskRequest dto) {
        OpenTextRequest open = (OpenTextRequest) dto;

        validateStatement(open.getStatement());
        CourseSummary course = validateCourseAndStatus(open.getCourseId(), open.getOrder());

        Task task = mapper.toOpenTextEntity(open);
        task.setCourseId(course.id());

        taskRepository.save(task);
    }
}
