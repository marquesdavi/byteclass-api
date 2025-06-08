package br.com.marques.byteclass.feature.course.dto;

import br.com.marques.byteclass.feature.course.entity.Course;
import br.com.marques.byteclass.feature.user.api.dto.UserSummary;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class CourseRequest {

    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    private String description;
    @NotNull
    @NotBlank
    @Email
    private String emailInstructor;

//    public Course fromRequest(UserSummary instructor) {
//        return new Course(this.getTitle(), this.getDescription(), instructor);
//    }
}
