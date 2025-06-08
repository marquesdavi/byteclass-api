package br.com.marques.byteclass.feature.course.port.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CourseRequest(
        @NotBlank @Size(min = 4, max = 50) String title,
        @NotBlank @Size(min = 4, max = 255) String description
) {
}
