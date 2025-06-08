package br.com.marques.byteclass.feature.task.api;

import br.com.marques.byteclass.feature.task.api.dto.*;
import jakarta.validation.Valid;

public interface TaskApi {

    void createOpenText(@Valid OpenTextRequest request);

    void createSingleChoice(@Valid ChoiceRequest request);

    void createMultipleChoice(@Valid ChoiceRequest request);

}
