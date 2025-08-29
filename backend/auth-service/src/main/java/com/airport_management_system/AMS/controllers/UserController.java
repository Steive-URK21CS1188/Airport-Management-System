package com.airport_management_system.AMS.controllers;

import com.airport_management_system.AMS.models.pojo.User;
import com.airport_management_system.AMS.models.dao.serviceImpl.*;
import com.airport_management_system.AMS.models.dao.services.*;
import com.airport_management_system.AMS.models.dto.LoginResponseDTO;
import com.airport_management_system.AMS.models.dto.UserLoginDTO;
import com.airport_management_system.AMS.models.dto.UserRegisterDTO;
import com.airport_management_system.AMS.models.dto.UserResponseDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    
    @Autowired
	private JwtService jwtService;
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRegisterDTO dto) {
        User savedUser = service.registerUser(dto);
        return mapToResponseDto(savedUser);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDto) {
        try {
            User user = service.authenticateUser(loginDto.getUsername(), loginDto.getPassword());

            // Generate JWT token
            String token = jwtService.generateToken(user.getUsername(), user.getRole());

            // Build response
            return ResponseEntity.ok(new LoginResponseDTO(token, user.getRole(), user.getUserId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

	
	@GetMapping("/role/{role}")
	public List<UserResponseDTO> getUsersByRole(@PathVariable String role) {
	    return service.getUsersByRole(role)
	                  .stream()
	                  .map(this::mapToResponseDto)
	                  .toList();
	}
	@GetMapping("/Validatetoken")
    public String validateToken(@RequestParam("Authorization") String token) {
      System.out.println("In Validation Token");
	     if(jwtService.validateToken(token)) {
	    	 System.out.println(jwtService.extractRoleFromToken(token));
	     return jwtService.extractRoleFromToken(token);
	     }
	     else
	    	return  "Not Valid";
	}
	
	@GetMapping("/dashboard")
    public UserResponseDTO getDashboardDetails(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        User user = service.getUserByUsername(username); 

        return mapToResponseDto(user);
    }
	@GetMapping("/id/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        User user = service.getUserById(id);
        return mapToResponseDto(user);
    }
    private UserResponseDTO mapToResponseDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setEmail(user.getAddress().getEmail());
        dto.setPhoneNo(user.getAddress().getPhoneno());
        dto.setHouseNo(user.getAddress().getHouseNo());
        dto.setStreet(user.getAddress().getStreet());
        dto.setCity(user.getAddress().getCity());
        dto.setState(user.getAddress().getState());
        dto.setPincode(user.getAddress().getPincode());
        return dto;
    }

}


