package br.com.marques.byteclass.feature.course.dto;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.course.entity.Status;

import java.io.Serializable;

public record CourseSummary(
        Long id,
        String title,
        String description,
        Status status
) implements Serializable {
    public static CourseSummary fromEntity(Course course){
        return new CourseSummary(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getStatus());
    }
}
