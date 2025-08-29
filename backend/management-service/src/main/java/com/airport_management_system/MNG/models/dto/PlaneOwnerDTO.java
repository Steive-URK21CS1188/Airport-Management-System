
package com.airport_management_system.MNG.models.dto;

//import com.airport_management_system.MNG.models.pojo.AddressDetails;

public class PlaneOwnerDTO {
    private Long ownerId;
    private String name;
    private AddressDetails address;
    
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
	
	public AddressDetails getAddress() {
		return address;
	}
	public void setAddress(AddressDetails address) {
		this.address = address;
	}
}
