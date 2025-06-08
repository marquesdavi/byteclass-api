package br.com.marques.byteclass.feature.course.controller;

import br.com.marques.byteclass.feature.course.dto.CourseSummary;
import br.com.marques.byteclass.feature.course.repository.CourseRepository;
import br.com.marques.byteclass.feature.course.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.common.util.ErrorItemDTO;
import br.com.marques.byteclass.feature.course.service.CourseService;
import br.com.marques.byteclass.feature.user.entity.User;
import br.com.marques.byteclass.feature.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class CourseController {
//    private final CourseService courseService;
//
//    @Secured({"ROLE_INSTRUCTOR"})
//    @PostMapping("/course/new")
//    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
//        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
//        courseService.createCourse(newCourse, loggedUser);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @GetMapping("/course/all")
//    public ResponseEntity<List<CourseListItemDTO>> listAllCourses() {
//        List<CourseListItemDTO> courses = courseService.listAllCourses();
//        return ResponseEntity.ok(courses);
//    }
//
//    @Secured({"ROLE_INSTRUCTOR"})
//    @PostMapping("/course/{id}/publish")
//    public ResponseEntity publishCourse(@PathVariable("id") Long id) {
//        courseService.publishCourse(id);
//        return ResponseEntity.ok().build();
//    }

}
