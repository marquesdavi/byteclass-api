package br.com.marques.byteclass.feature.task.app.strategy;

import br.com.marques.byteclass.feature.course.port.CoursePort;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.task.port.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.port.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.port.mapper.OptionMapper;
import br.com.marques.byteclass.feature.task.port.mapper.TaskRequestMapper;
import br.com.marques.byteclass.feature.task.domain.Choice;
import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.adapter.repository.TaskRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class MultipleChoiceTaskStrategy extends AbstractTaskStrategy {
    private final TaskRequestMapper requestMapper;

    public MultipleChoiceTaskStrategy(TaskRepository taskRepository,
                                      OptionMapper optionMapper,
                                      @Lazy CoursePort courseApi,
                                      TaskRequestMapper requestMapper) {
        super(taskRepository, optionMapper, courseApi);
        this.requestMapper = requestMapper;
    }

    @Override
    public Type getType() {
        return Type.MULTIPLE_CHOICE;
    }

    @Override
    @Transactional
    public void save(TaskRequest dto) {
        ChoiceRequest choice = (ChoiceRequest) dto;

        validateStatement(choice.getStatement());
        validateOptionsSize(choice.getOptions(), getType());
        validateUniqueOptions(choice.getOptions());

        long correctCount = countCorrect(choice);
        if (correctCount < 2) {
            throw new IllegalArgumentException("At least two correct answers required");
        }

        CourseSummary course = validateCourseAndStatus(choice.getCourseId(), choice.getOrder());

        Task task = requestMapper.toMultipleChoiceEntity(choice);
        task.setCourseId(course.id());

        Task saved = taskRepository.save(task);
        List<Choice> choices = getChoices(choice, saved);
        saved.setChoices(choices);

        taskRepository.save(saved);
    }
}
