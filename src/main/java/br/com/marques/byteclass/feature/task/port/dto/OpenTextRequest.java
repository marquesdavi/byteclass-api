package br.com.marques.byteclass.feature.task.port.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OpenTextRequest implements TaskRequest {
    @NotNull
    private Long courseId;

    @NotNull
    @Size(min = 4, max = 255, message = "Statement must be between 4 and 255 characters")
    private String statement;

    @NotNull
    @Min(value = 1, message = "Order must be an integer and greater than 0")
    private Integer order;
}
