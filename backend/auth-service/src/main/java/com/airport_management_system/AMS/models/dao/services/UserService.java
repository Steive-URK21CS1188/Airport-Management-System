package com.airport_management_system.AMS.models.dao.services;

import java.util.List;

import com.airport_management_system.AMS.models.dto.UserRegisterDTO;
import com.airport_management_system.AMS.models.pojo.User;

public interface UserService {

    // Accept DTO for registration
    User registerUser(UserRegisterDTO dto);

    String login(String username, String password);

    User getUserById(Long id);
    List<User> getUsersByRole(String role);
    void validateTokenSignature(String token);
    String extractRoleFromToken(String token);
    User getUserByUsername(String username);
    User authenticateUser(String username, String password);
    
}
