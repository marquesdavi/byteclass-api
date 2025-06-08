package br.com.marques.byteclass.feature.task.api.dto;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.task.entity.Task;
import br.com.marques.byteclass.feature.task.entity.Type;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Objects;

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

    public ChoiceRequest() {
    }

    public ChoiceRequest(Long courseId, String statement, Integer order, List<OptionDto> options) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
        this.options = options;
    }

    public Task toEntity(Course course, Type taskType) {
        return Task.builder()
                .courseId(course)
                .statement(this.statement)
                .taskOrder(this.order)
                .taskType(taskType)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChoiceRequest choiceRequest = (ChoiceRequest) o;
        return Objects.equals(courseId, choiceRequest.courseId) && Objects.equals(statement, choiceRequest.statement) && Objects.equals(order, choiceRequest.order) && Objects.equals(options, choiceRequest.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, statement, order, options);
    }
}
