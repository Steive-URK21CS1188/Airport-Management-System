package com.airport_management_system.MNG.models.dto;

import com.airport_management_system.MNG.models.dto.AddressDetails; 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String username;
    private String role;
    private String dateOfBirth;
    private AddressDetails address;
}
