package br.com.marques.byteclass.feature.task.api.dto;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.task.entity.Task;
import br.com.marques.byteclass.feature.task.entity.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Objects;

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

    public OpenTextRequest() {
    }

    public OpenTextRequest(Long courseId, String statement, Integer order) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
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
        OpenTextRequest that = (OpenTextRequest) o;
        return Objects.equals(courseId, that.courseId) && Objects.equals(statement, that.statement) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, statement, order);
    }
}
