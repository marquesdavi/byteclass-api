package br.com.marques.byteclass.feature.task.controller;

import br.com.marques.byteclass.feature.task.api.TaskApi;
import br.com.marques.byteclass.feature.task.api.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
@Validated
@RequiredArgsConstructor
public class TaskController {
    private final TaskApi taskApi;

    @PostMapping("/new/opentext")
    public ResponseEntity<Void> newOpenText(@Valid @RequestBody OpenTextRequest dto) {
        taskApi.createOpenText(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity<Void> newSingleChoice(@Valid @RequestBody ChoiceRequest dto) {
        taskApi.createSingleChoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/new/multiplechoice")
    public ResponseEntity<Void> newMultipleChoice(@Valid @RequestBody ChoiceRequest dto) {
        taskApi.createMultipleChoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
