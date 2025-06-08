package br.com.marques.byteclass.feature.task.service;

import br.com.marques.byteclass.feature.task.api.TaskApi;
import br.com.marques.byteclass.feature.task.api.dto.*;
import br.com.marques.byteclass.feature.task.entity.Type;
import br.com.marques.byteclass.feature.task.service.strategy.TaskTypeStrategy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
public class TaskServiceImpl implements TaskApi {

    private final Map<Type, TaskTypeStrategy> strategies;

    public TaskServiceImpl(java.util.List<TaskTypeStrategy> list) {
        this.strategies = list.stream()
                .collect(Collectors.toMap(TaskTypeStrategy::getType, Function.identity()));
    }

    @Override
    @Transactional
    public void createOpenText(@Valid OpenTextRequest request) {
        strategies.get(Type.OPEN_TEXT).save(request);
    }

    @Override
    @Transactional
    public void createSingleChoice(@Valid ChoiceRequest request) {
        strategies.get(Type.SINGLE_CHOICE).save(request);
    }

    @Override
    @Transactional
    public void createMultipleChoice(@Valid ChoiceRequest request) {
        strategies.get(Type.MULTIPLE_CHOICE).save(request);
    }
}
