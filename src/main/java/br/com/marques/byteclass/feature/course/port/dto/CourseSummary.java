package br.com.marques.byteclass.feature.course.port.dto;

import br.com.marques.byteclass.feature.course.domain.Status;
import lombok.Builder;

@Builder
public record CourseSummary(
        Long id,
        String title,
        String description,
        Status status
) { }
