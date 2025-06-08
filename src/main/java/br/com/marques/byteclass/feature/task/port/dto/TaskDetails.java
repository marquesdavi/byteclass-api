package br.com.marques.byteclass.feature.task.port.dto;

import br.com.marques.byteclass.feature.task.domain.Choice;
import br.com.marques.byteclass.feature.task.domain.Type;

import java.util.List;

public record TaskDetails(
        Long id,
        Long courseId,
        String statement,
        Integer taskOrder,
        Type taskType,
        List<Choice> choices
) {}

