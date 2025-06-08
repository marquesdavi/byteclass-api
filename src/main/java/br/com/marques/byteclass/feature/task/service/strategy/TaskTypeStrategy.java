package br.com.marques.byteclass.feature.task.service.strategy;

import br.com.marques.byteclass.feature.task.api.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.entity.Type;

public interface TaskTypeStrategy {
    Type getType();
    void save(TaskRequest dto);
}
