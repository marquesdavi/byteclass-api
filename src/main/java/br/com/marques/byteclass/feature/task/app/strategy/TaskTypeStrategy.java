package br.com.marques.byteclass.feature.task.app.strategy;

import br.com.marques.byteclass.feature.task.port.dto.TaskRequest;
import br.com.marques.byteclass.feature.task.domain.Type;

public interface TaskTypeStrategy {
    Type getType();
    void save(TaskRequest dto);
}
