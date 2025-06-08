package br.com.marques.byteclass.feature.task.app.strategy;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.GenericException;
import br.com.marques.byteclass.feature.course.port.CoursePort;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import br.com.marques.byteclass.feature.course.domain.Status;
import br.com.marques.byteclass.feature.task.port.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.port.dto.OptionDto;
import br.com.marques.byteclass.feature.task.port.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.port.mapper.OptionMapper;
import br.com.marques.byteclass.feature.task.domain.Choice;
import br.com.marques.byteclass.feature.task.domain.Task;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.adapter.repository.TaskRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractTaskStrategy implements TaskTypeStrategy {
    protected final TaskRepository taskRepository;
    protected final OptionMapper optionMapper;
    @Lazy
    protected final CoursePort courseApi;

    public AbstractTaskStrategy(TaskRepository taskRepository,
                                OptionMapper optionMapper,
                                @Lazy CoursePort courseApi) {
        this.taskRepository = taskRepository;
        this.optionMapper = optionMapper;
        this.courseApi = courseApi;
    }

    protected void validateStatement(String statement) {
        taskRepository.findTaskByStatement(statement)
                .ifPresent(t -> {
                    throw new AlreadyExistsException("Task with this statement already exists");
                });
    }

    protected CourseSummary validateCourseAndStatus(Long courseId, Integer order) {
        CourseSummary course = courseApi.getById(courseId);

        if (!Status.BUILDING.equals(course.status())) {
            throw new GenericException("Course is not in BUILDING status", HttpStatus.BAD_REQUEST);
        }

        List<Task> existing = taskRepository.findAllByCourseIdOrderByTaskOrder(courseId);
        if (order > existing.size() + 1) {
            throw new GenericException("Invalid order: sequence is not continuous", HttpStatus.BAD_REQUEST);
        }

        handleOrder(order, existing);
        taskRepository.saveAll(existing);

        return course;
    }

    private static void handleOrder(Integer order, List<Task> existing) {
        existing.stream()
                .filter(t -> t.getTaskOrder() >= order)
                .forEach(t -> t.setTaskOrder(t.getTaskOrder() + 1));
    }

    protected void validateOptionsSize(List<?> options, Type type) {
        int min = Type.SINGLE_CHOICE.equals(type) ? 2 : 3;
        int max = 5;
        if (options.size() < min || options.size() > max) {
            throw new IllegalArgumentException(
                    "The number of options must be between " + min + " and " + max);
        }
    }

    protected void validateUniqueOptions(List<?> options) {
        Set<Object> uniques = options.stream().map(Object::toString).collect(Collectors.toSet());
        if (uniques.size() != options.size()) {
            throw new AlreadyExistsException("Duplicate options are not allowed");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public abstract void save(TaskRequest dto);

    @NotNull
    List<Choice> getChoices(ChoiceRequest choice, Task saved) {
        return choice.getOptions().stream()
                .map(optDto -> {
                    Choice c = optionMapper.toEntity(optDto);
                    c.setTask(saved);
                    return c;
                })
                .collect(Collectors.toList());
    }

    static long countCorrect(ChoiceRequest choice) {
        return choice.getOptions().stream()
                .filter(OptionDto::isCorrect)
                .count();
    }
}
