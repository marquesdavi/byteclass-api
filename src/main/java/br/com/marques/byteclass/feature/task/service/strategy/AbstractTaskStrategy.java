package br.com.marques.byteclass.feature.task.service.strategy;

import br.com.marques.byteclass.common.exception.*;
import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.course.repository.CourseRepository;
import br.com.marques.byteclass.feature.task.api.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.entity.Choice;
import br.com.marques.byteclass.feature.task.entity.Task;
import br.com.marques.byteclass.feature.task.entity.Type;
import br.com.marques.byteclass.feature.task.api.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractTaskStrategy implements TaskTypeStrategy {
    protected final TaskRepository   taskRepository;
    protected final CourseRepository courseRepository;

    protected void validateStatement(String statement) {
        taskRepository.findTaskByStatement(statement)
                .ifPresent(t -> { throw new AlreadyExistsException("Task with this statement already exists"); });
    }

    protected Course validateCourseAndStatus(Long courseId, Integer order) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        if (!course.getStatus().name().equals("BUILDING")) {
            throw new GenericException("Course is not in BUILDING status", HttpStatus.BAD_REQUEST);
        }

        var existing = taskRepository.findAllByCourseIdOrderByTaskOrder(course);
        if (order > existing.size() + 1) {
            throw new GenericException("Invalid order: sequence is not continuous", HttpStatus.BAD_REQUEST);
        }

        existing.stream()
                .filter(t -> t.getTaskOrder() >= order)
                .forEach(t -> t.setTaskOrder(t.getTaskOrder() + 1));
        taskRepository.saveAll(existing);
        return course;
    }

    protected void validateOptionsSize(List<?> options, Type type) {
        int min = (type == Type.SINGLE_CHOICE ? 2 : 3), max = 5;
        if (options.size() < min || options.size() > max) {
            throw new IllegalArgumentException("The number of options must be between " + min + " and " + max);
        }
    }

    protected void validateUniqueOptions(List<?> options) {
        var uniques = options.stream().map(Object::toString).collect(Collectors.toSet());
        if (uniques.size() != options.size()) {
            throw new AlreadyExistsException("Duplicate options are not allowed");
        }
    }

    @NotNull
    static List<Choice> getChoices(ChoiceRequest choice, Task saved) {
        return choice.getOptions().stream()
                .map(opt -> opt.toEntity(saved))
                .toList();
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public abstract void save(TaskRequest dto);
}
