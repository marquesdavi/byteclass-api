package br.com.marques.byteclass.feature.task.port.dto;

import br.com.marques.byteclass.feature.task.domain.Type;

public record TaskSummary(
        Long courseId,
        String statement,
        Integer taskOrder,
        Type taskType
) {}

