package br.com.marques.byteclass.feature.task.api.dto;

import br.com.marques.byteclass.feature.task.entity.Choice;
import br.com.marques.byteclass.feature.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class OptionDto {
    @NotNull
    @Size(min = 4, max = 80, message = "Option must be between 4 and 80 characters")
    private String option;

    @NotNull
    private Boolean isCorrect;

    public OptionDto() {
    }

    public OptionDto(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public Choice toEntity(Task task) {
        return Choice.builder()
                .content(this.option)
                .isCorrect(this.isCorrect)
                .build();
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @JsonProperty("isCorrect")
    public Boolean isCorrect() {
        return isCorrect;
    }

    @JsonProperty("isCorrect")
    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OptionDto option1 = (OptionDto) o;
        return Objects.equals(option, option1.option) && Objects.equals(isCorrect, option1.isCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(option, isCorrect);
    }
}
