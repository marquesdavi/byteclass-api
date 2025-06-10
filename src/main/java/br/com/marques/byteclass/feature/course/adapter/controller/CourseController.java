package br.com.marques.byteclass.feature.course.adapter.controller;

import br.com.marques.byteclass.feature.course.port.CoursePort;
import br.com.marques.byteclass.feature.course.port.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.port.dto.CourseSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@Tag(name = "Course", description = "Endpoints for managing courses")
public class CourseController {
    private final CoursePort coursePort;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a new course", description = "Only authenticated instructors can create courses.")
    @ApiResponse(responseCode = "201", description = "Course created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping
    @CacheEvict(value = "courses", allEntries = true)
    public ResponseEntity<Long> create(
            @Valid @RequestBody CourseRequest dto
    ) {
        Long id = coursePort.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @Operation(
            summary = "List courses with pagination",
            description = "Retrieves a page of courses. Use `page`, `size` and `sort` query parameters."
    )
    @ApiResponse(responseCode = "200", description = "Page of courses retrieved successfully")
    @GetMapping
    @Cacheable(value = "courses")
    public Page<CourseSummary> list(
            @ParameterObject Pageable pageable
    ) {
        return coursePort.list(pageable);
    }

    @Operation(summary = "Get course by ID", description = "Retrieves details of a specific course by its ID.")
    @ApiResponse(responseCode = "200", description = "Course found")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @GetMapping("/{id}")
    @Cacheable(value = "course", key = "#id")
    public CourseSummary get(
            @Parameter(description = "ID of the course to retrieve", required = true)
            @PathVariable Long id
    ) {
        return coursePort.getById(id);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Update a course", description = "Updates the title and description of an existing course.")
    @ApiResponse(responseCode = "200", description = "Course updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PatchMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = "course", key = "#id"),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public ResponseEntity<Void> update(
            @Parameter(description = "ID of the course to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest dto
    ) {
        coursePort.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Delete a course", description = "Deletes a course by its ID.")
    @ApiResponse(responseCode = "200", description = "Course deleted successfully")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @DeleteMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(value = "course", key = "#id"),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the course to delete", required = true)
            @PathVariable Long id
    ) {
        coursePort.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Publish a course", description = "Publishes a course, making it available to students. Course must have at least one task of each type and correct ordering.")
    @ApiResponse(responseCode = "200", description = "Course published successfully")
    @ApiResponse(responseCode = "400", description = "Cannot publish course (invalid state or tasks)")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PostMapping("/{id}/publish")
    @Caching(evict = {
            @CacheEvict(value = "course", key = "#id"),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public ResponseEntity<Void> publish(
            @Parameter(description = "ID of the course to publish", required = true)
            @PathVariable Long id
    ) {
        coursePort.publish(id);
        return ResponseEntity.ok().build();
    }
}
