package br.com.marques.byteclass.feature.user.entity;

import br.com.marques.byteclass.feature.user.api.dto.UserRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User(String name, String mail, Role role) {
        User.builder()
                .name(name)
                .email(mail)
                .role(role)
                .build();
    }

    public static User fromRequest(UserRequest userRequest) {
        if (Objects.isNull(userRequest)) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        user.setPassword(new BCryptPasswordEncoder().encode(userRequest.password()));
        return user;
    }

    public boolean isInstructor() {
        return Role.INSTRUCTOR.equals(this.role);
    }
}
