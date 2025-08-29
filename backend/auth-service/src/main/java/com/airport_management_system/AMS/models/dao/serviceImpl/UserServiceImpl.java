package com.airport_management_system.AMS.models.dao.serviceImpl;


import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.airport_management_system.AMS.models.dao.services.UserService;
import com.airport_management_system.AMS.models.dto.UserRegisterDTO;
import com.airport_management_system.AMS.models.pojo.User;
import com.airport_management_system.AMS.models.pojo.AddressDetails;
import com.airport_management_system.AMS.models.repositories.UserRepository;
import com.airport_management_system.AMS.models.repositories.AddressRepository;

@Service
public class UserServiceImpl implements UserService { 

    private final UserRepository userRepository; 
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtService jwtService; 

    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) { 
        this.userRepository = userRepository; 
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder; 
        this.jwtService = jwtService; 
    } 

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    @Override
    public User registerUser(UserRegisterDTO dto) {
    	
    	 if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
    	        throw new RuntimeException("Username already exists: " + dto.getUsername());
    	    }
    	 
        AddressDetails address = new AddressDetails();
        address.setHouseNo(dto.getHouseNo());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());
        address.setEmail(dto.getEmail());
        address.setPhoneno(dto.getPhoneno());
        addressRepository.save(address);

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole().toUpperCase());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(address);

        return userRepository.save(user);
    }


    @Override
    public String login(String username, String password) { 
        Optional<User> user = userRepository.findByUsername(username); 
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) { 
            return jwtService.generateToken(username, user.get().getRole()); 
        } 
        throw new RuntimeException("Invalid credentials"); 
    } 
    
    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCase(role);
    }

    @Override
    public User getUserById(Long id) { 
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); 
    } 

    @Override
    public void validateTokenSignature(String token) {
        //jwtService.validateTokenSignature(token); 
    }

    @Override
    public String extractRoleFromToken(String token) {
        return jwtService.extractRoleFromToken(token);
    }
    @Override
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String storedPassword = user.getPassword();

        // Support both hashed and plain passwords for old data
        boolean passwordMatches;
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            // BCrypt hash
            passwordMatches = passwordEncoder.matches(password, storedPassword);
        } else {
            // Plain text comparison for old DB data
            passwordMatches = storedPassword.equals(password);
        }

        if (!passwordMatches) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }


}

