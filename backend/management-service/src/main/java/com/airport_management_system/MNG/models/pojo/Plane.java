package com.airport_management_system.MNG.models.pojo;

import com.airport_management_system.MNG.models.dto.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plane_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planeId;

    @NotBlank(message = "Plane Number is required")
    private String planeNumber;
    
    @NotBlank(message = "Model Number is required")
    private String model;
    
    @Min(value = 10, message = "Minimum Capacity must be at least 10")
    @Max(value = 50, message = "Maximum Capacity cannot exceed 50")
    @Column(nullable = false)
    private int capacity;
    
    @Column(name = "user_id")
    private Long userId;

    


    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PlaneOwner owner;




	


	public Long getPlaneId() {
		return planeId;
	}

	public void setPlaneId(Long planeId) {
		this.planeId = planeId;
	}

	public String getPlaneNumber() {
		return planeNumber;
	}

	public void setPlaneNumber(String planeNumber) {
		this.planeNumber = planeNumber;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public PlaneOwner getOwner() {
		return owner;
	}

	public void setOwner(PlaneOwner owner) {
		this.owner = owner;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

    
}
