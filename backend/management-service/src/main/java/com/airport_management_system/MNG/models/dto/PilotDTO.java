package com.airport_management_system.MNG.models.dto;

//import com.airport_management_system.MNG.models.pojo.AddressDetails;
import jakarta.validation.constraints.NotBlank;

public class PilotDTO {

    private Long pilotId;

    @NotBlank(message = "Pilot name cannot be blank")
    private String name;

    @NotBlank(message = "License number cannot be blank")
    private String licenseNo;

    private Long userId;
    private AddressDetails address;

    // Getters and Setters
    public Long getPilotId() {
        return pilotId;
    }

    public void setPilotId(Long pilotId) {
        this.pilotId = pilotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AddressDetails getAddress() {
        return address;
    }

    public void setAddress(AddressDetails address) {
        this.address = address;
    }
}
