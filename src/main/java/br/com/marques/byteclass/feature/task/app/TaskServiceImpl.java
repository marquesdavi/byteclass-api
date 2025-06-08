package br.com.marques.byteclass.feature.task.app;

import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.config.resilience.Resilient;
import br.com.marques.byteclass.feature.task.port.TaskPort;
import br.com.marques.byteclass.feature.task.port.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.port.dto.OpenTextRequest;
import br.com.marques.byteclass.feature.task.port.dto.TaskDetails;
import br.com.marques.byteclass.feature.task.port.dto.TaskSummary;
import br.com.marques.byteclass.feature.task.port.mapper.TaskResponseMapper;
import br.com.marques.byteclass.feature.task.domain.Type;
import br.com.marques.byteclass.feature.task.adapter.repository.TaskRepository;
import br.com.marques.byteclass.feature.task.app.strategy.TaskTypeStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
public class TaskServiceImpl implements TaskPort {
    private final Map<Type, TaskTypeStrategy> strategies;
    private final TaskRepository taskRepository;
    private final TaskResponseMapper summaryMapper;

    public TaskServiceImpl(List<TaskTypeStrategy> list,
                           TaskRepository taskRepository,
                           TaskResponseMapper taskResponseMapper) {
        this.strategies = getStrategies(list);
        this.taskRepository = taskRepository;
        this.summaryMapper = taskResponseMapper;
    }

    @NotNull
    private static Map<Type, TaskTypeStrategy> getStrategies(List<TaskTypeStrategy> list) {
        return list.stream()
                .collect(Collectors.toMap(TaskTypeStrategy::getType, Function.identity()));
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void createOpenText(@Valid OpenTextRequest request) {
        strategies.get(Type.OPEN_TEXT).save(request);
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void createSingleChoice(@Valid ChoiceRequest request) {
        strategies.get(Type.SINGLE_CHOICE).save(request);
    }

    @Override
    @Transactional
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public void createMultipleChoice(@Valid ChoiceRequest request) {
        strategies.get(Type.MULTIPLE_CHOICE).save(request);
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public List<TaskSummary> listByCourseId(@Min(value = 1, message = "Id must be greater than 0") Long courseId) {
        return taskRepository.findAllByCourseIdOrderByTaskOrder(courseId)
                .stream()
                .map(summaryMapper::toDto)
                .toList();
    }

    @Override
    @Resilient(rateLimiter = "RateLimiter", circuitBreaker = "CircuitBreaker")
    public TaskDetails getById(@Min(value = 1, message = "Id must be greater than 0") Long id) {
        return taskRepository.findById(id)
                .map(summaryMapper::toDetailsDto)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }
}
