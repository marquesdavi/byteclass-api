package br.com.marques.byteclass.feature.task.service.strategy;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.course.repository.CourseRepository;
import br.com.marques.byteclass.feature.task.api.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.api.dto.OptionDto;
import br.com.marques.byteclass.feature.task.api.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.entity.Choice;
import br.com.marques.byteclass.feature.task.entity.Task;
import br.com.marques.byteclass.feature.task.entity.Type;
import br.com.marques.byteclass.feature.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SingleChoiceTaskStrategy extends AbstractTaskStrategy {

    public SingleChoiceTaskStrategy(TaskRepository tr, CourseRepository cr) {
        super(tr, cr);
    }

    @Override
    public Type getType() {
        return Type.SINGLE_CHOICE;
    }

    @Override
    public void save(TaskRequest dto) {
        ChoiceRequest choice = (ChoiceRequest) dto;
        validateStatement(choice.getStatement());
        validateOptionsSize(choice.getOptions(), Type.SINGLE_CHOICE);
        validateUniqueOptions(choice.getOptions());

        if (choice.getOptions().stream().filter(OptionDto::isCorrect).count() != 1) {
            throw new IllegalArgumentException("Must have exactly 1 correct answer");
        }

        Course course = validateCourseAndStatus(choice.getCourseId(), choice.getOrder());
        Task task = choice.toEntity(course, Type.SINGLE_CHOICE);
        Task saved = taskRepository.save(task);
        List<Choice> list = getChoices(choice, saved);
        saved.setChoices(list);
        taskRepository.save(saved);
    }


}

