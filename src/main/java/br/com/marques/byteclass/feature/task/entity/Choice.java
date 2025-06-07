package br.com.marques.byteclass.feature.task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "tb_choice")
@NoArgsConstructor
@AllArgsConstructor
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
