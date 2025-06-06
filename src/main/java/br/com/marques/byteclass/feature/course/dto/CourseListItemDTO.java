package br.com.marques.byteclass.feature.course.dto;

import br.com.marques.byteclass.feature.course.entity.Status;
import br.com.marques.byteclass.feature.course.entity.Course;

import java.io.Serializable;

public record CourseListItemDTO(
        Long id,
        String title,
        String description,
        Status status
) implements Serializable {
}
