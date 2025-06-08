package br.com.marques.byteclass.feature.task.port.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ChoiceRequest implements TaskRequest {
    @NotNull
    private Long courseId;

    @NotNull
    @Size(min = 4, max = 255, message = "Statement must be between 4 and 255 characters")
    private String statement;

    @NotNull
    @Min(value = 1, message = "Order must be an integer and greater than 0")
    private Integer order;

    @NotNull
    @Size(min = 2, max = 5, message = "The number of options must be between 2 and 5")
    @Valid
    private List<OptionDto> options;
}
