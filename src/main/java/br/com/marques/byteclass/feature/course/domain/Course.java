package br.com.marques.byteclass.feature.course.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tb_course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;
    @Enumerated(EnumType.STRING)
    private Status status = Status.BUILDING;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
