package com.airport_management_system.MNG.models.pojo;

import java.util.List;

import com.airport_management_system.MNG.models.dto.AddressDetails;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "plane_owner_details")
public class PlaneOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ownerId;

    @NotBlank(message = "Owner Name is required")
    private String name;
   

    // Transient DTO, not persisted or mapped by JPA
    @Transient
    private AddressDetails address;
    private Long addressId;
    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "addressId", insertable = false, updatable = false)
    private AddressDetails address;*/

    @JsonManagedReference
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Plane> planes;

    public void setAddress(AddressDetails address) {
        this.address = address;
        if (address != null) {
            this.addressId = address.getAddressId();  // set FK
        }
    }

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public List<Plane> getPlanes() {
		return planes;
	}

	public void setPlanes(List<Plane> planes) {
		this.planes = planes;
	}

	public AddressDetails getAddress() {
		return address;
	}

    
    
}
