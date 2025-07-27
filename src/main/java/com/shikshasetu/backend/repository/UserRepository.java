package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.Role;
import com.shikshasetu.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long countByRole(String role);
    long countByRole(Role role);
}
