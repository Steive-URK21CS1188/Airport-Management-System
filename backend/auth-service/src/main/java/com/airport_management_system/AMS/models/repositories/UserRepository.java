package com.airport_management_system.AMS.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.airport_management_system.AMS.models.pojo.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRoleIgnoreCase(String role);
}
