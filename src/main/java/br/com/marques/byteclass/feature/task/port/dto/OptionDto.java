package br.com.marques.byteclass.feature.task.port.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OptionDto {
    @NotNull
    @Size(min = 4, max = 80, message = "Option must be between 4 and 80 characters")
    private String option;

    @NotNull
    private Boolean isCorrect;

    @JsonProperty("isCorrect")
    public Boolean isCorrect() {
        return isCorrect;
    }

    @JsonProperty("isCorrect")
    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
