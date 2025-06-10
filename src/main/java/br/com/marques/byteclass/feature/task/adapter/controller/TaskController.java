package br.com.marques.byteclass.feature.task.adapter.controller;

import br.com.marques.byteclass.feature.task.port.TaskPort;
import br.com.marques.byteclass.feature.task.port.dto.ChoiceRequest;
import br.com.marques.byteclass.feature.task.port.dto.OpenTextRequest;
import br.com.marques.byteclass.feature.task.port.dto.TaskDetails;
import br.com.marques.byteclass.feature.task.port.dto.TaskSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Endpoints for managing course activities (tasks)")
public class TaskController {
    private final TaskPort taskPort;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create an open-text task",
            description = "Adds a new open-text activity to the specified course in BUILDING status.")
    @ApiResponse(responseCode = "201", description = "Open-text task created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request or course not in BUILDING status")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PostMapping("/new/opentext")
    @CacheEvict(value = {"tasksByCourse", "taskDetails"}, allEntries = true)
    public ResponseEntity<Void> newOpenText(
            @Valid @RequestBody OpenTextRequest dto
    ) {
        taskPort.createOpenText(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a single-choice task",
            description = "Adds a new single-choice activity (exactly one correct option) to the specified course.")
    @ApiResponse(responseCode = "201", description = "Single-choice task created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request or validation error")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PostMapping("/new/singlechoice")
    @CacheEvict(value = {"tasksByCourse", "taskDetails"}, allEntries = true)
    public ResponseEntity<Void> newSingleChoice(
            @Valid @RequestBody ChoiceRequest dto
    ) {
        taskPort.createSingleChoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a multiple-choice task",
            description = "Adds a new multiple-choice activity (at least two correct options) to the specified course.")
    @ApiResponse(responseCode = "201", description = "Multiple-choice task created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request or validation error")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @PostMapping("/new/multiplechoice")
    @CacheEvict(value = {"tasksByCourse", "taskDetails"}, allEntries = true)
    public ResponseEntity<Void> newMultipleChoice(
            @Valid @RequestBody ChoiceRequest dto
    ) {
        taskPort.createMultipleChoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "List tasks by course",
            description = "Retrieves all tasks for the given course, in order.")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Course not found")
    @GetMapping("/course/{id}")
    @Cacheable(value = "tasksByCourse", key = "#id")
    public ResponseEntity<List<TaskSummary>> listByCourse(
            @Parameter(description = "ID of the course whose tasks to list", required = true)
            @PathVariable("id") Long id
    ) {
        List<TaskSummary> summaries = taskPort.listByCourseId(id);
        return ResponseEntity.ok(summaries);
    }

    @Operation(summary = "Get task details by ID",
            description = "Retrieves full details of a specific task by its ID.")
    @ApiResponse(responseCode = "200", description = "Task details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/{id}")
    @Cacheable(value = "taskDetails", key = "#id")
    public ResponseEntity<TaskDetails> getById(
            @Parameter(description = "ID of the task to retrieve", required = true)
            @PathVariable("id") Long id
    ) {
        TaskDetails details = taskPort.getById(id);
        return ResponseEntity.ok(details);
    }
}
