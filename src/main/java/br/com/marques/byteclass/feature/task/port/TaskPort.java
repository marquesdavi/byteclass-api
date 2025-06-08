package br.com.marques.byteclass.feature.task.port;

import br.com.marques.byteclass.feature.task.port.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface TaskPort {
    void createOpenText(@Valid OpenTextRequest request);
    void createSingleChoice(@Valid ChoiceRequest request);
    void createMultipleChoice(@Valid ChoiceRequest request);
    List<TaskSummary> listByCourseId(@Min(value = 1, message = "Id must be greater than 0") Long courseId);
    TaskDetails getById(@Min(value = 1, message = "Id must be greater than 0") Long id);
}
