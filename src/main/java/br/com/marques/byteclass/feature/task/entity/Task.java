package br.com.marques.byteclass.feature.task.entity;

import br.com.marques.byteclass.feature.course.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "tb_task")
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course courseId;
    private String statement;
    @Column(name = "task_order", nullable = false)
    private Integer taskOrder;
    @Column(name = "task_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type taskType;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices = new ArrayList<>();
}
