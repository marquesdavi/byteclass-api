package br.com.marques.byteclass.feature.course.service;

import br.com.marques.byteclass.common.exception.AlreadyExistsException;
import br.com.marques.byteclass.common.exception.NotFoundException;
import br.com.marques.byteclass.feature.user.api.AuthenticationApi;
import br.com.marques.byteclass.feature.course.dto.CourseRequest;
import br.com.marques.byteclass.feature.course.dto.CourseSummary;
import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.course.entity.Status;
import br.com.marques.byteclass.feature.course.repository.CourseRepository;
import br.com.marques.byteclass.feature.task.entity.Task;
import br.com.marques.byteclass.feature.task.repository.TaskRepository;
import br.com.marques.byteclass.feature.user.api.dto.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService<Long, Course, CourseRequest, CourseSummary> {
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final AuthenticationApi authService;

    @Override
    public List<CourseSummary> list() {
        return courseRepository.findAll().stream()
                .map(CourseSummary::fromEntity)
                .toList();
    }

    @Override
    public void update(Long aLong, CourseRequest dto) {

    }

    @Override
    public CourseSummary getByID(Long aLong) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Transactional(rollbackFor = Exception.class)
    public void publishCourse(Long id) {
        Optional<Course> hasCourse = courseRepository.findById(id);

        hasCourse.ifPresentOrElse(course -> {
                    if (course.getStatus().name().equals(Status.PUBLISHED.name())) {
                        throw new AlreadyExistsException("Course already published.");
                    }

                    List<Task> tasks = taskRepository.findAllByCourseIdOrderByTaskOrder(course);
                    validateOrderAndComposition(tasks);

                    course.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                    course.setStatus(Status.PUBLISHED);
                    courseRepository.save(course);
                },
                () -> {
                    throw new NotFoundException("Course not found.");
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CourseRequest newCourse) {
        Optional<UserSummary> possibleAuthor = Optional.ofNullable(authService.getAuthenticated());

        if(possibleAuthor.isEmpty() || !possibleAuthor.get().isInstructor()) {
            throw new IllegalArgumentException("The user is not an instructor.");
        }

        Course course = null; //newCourse.fromRequest(possibleAuthor.get());
        courseRepository.save(course);
    }

    private void validateOrderAndComposition(List<Task> tasks) {
        if (tasks.isEmpty()) {
            throw new NotFoundException("Course doesn't have any task.");
        }

        int openText = 0;
        int singleChoice = 0;
        int multipleChoice = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskOrder() != i + 1) {
                throw new IllegalArgumentException("Tasks are not in the correct order.");
            }
            switch (tasks.get(i).getTaskType().name()) {
                case "OPEN_TEXT" -> openText++;
                case "SINGLE_CHOICE" -> singleChoice++;
                case "MULTIPLE_CHOICE" -> multipleChoice++;
            }
        }

        if (openText == 0 || singleChoice == 0 || multipleChoice == 0) {
            throw new IllegalArgumentException("Course must have at least one task of each type.");
        }
    }
}
