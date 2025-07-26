package com.shikshasetu.backend.model;

import jakarta.persistence.*;
import lombok.*;

// import java.time.LocalDateTime;
// import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // STUDENT, TRAINER, ADMIN

    @Builder.Default
    private boolean active = true;
    // @Builder.Default
    // private LocalDateTime registeredAt = LocalDateTime.now();

    // @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    // private List<Course> courses;

    // @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    // private List<Enrollment> enrollments;
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRole(Role role) {
    this.role = role;
    }
}
