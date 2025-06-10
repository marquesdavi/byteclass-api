package br.com.marques.byteclass.common.util;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ParameterObject
public record PageableRequest(
    @Parameter(description = "Page number, zero-based", example = "0")
    @PositiveOrZero Integer page,

    @Parameter(description = "Page size", example = "10")
    @Positive Integer size,

    @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
    String direction,

    @Parameter(description = "Field to sort by", example = "title")
    String orderBy
) {
    public Pageable toPageable() {
        Sort.Direction dir = Sort.Direction.fromString(direction);
        return PageRequest.of(page, size, Sort.by(dir, orderBy));
    }
}