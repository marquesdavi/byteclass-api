package br.com.marques.byteclass.feature.task.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    @Column(nullable = false, length = 255)
    private String statement;
    @Column(name = "task_order", nullable = false)
    private Integer taskOrder;
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private Type taskType;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Choice> choices = new ArrayList<>();
}
