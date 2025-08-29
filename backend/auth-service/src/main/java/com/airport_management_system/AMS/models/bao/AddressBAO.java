package com.airport_management_system.AMS.models.bao;

import com.airport_management_system.AMS.models.pojo.AddressDetails;

import java.util.List;

public interface AddressBAO {
    AddressDetails addAddress(AddressDetails address, String role);
    AddressDetails getByEmail(String email, String role);
    List<AddressDetails> getAll(String role);
    AddressDetails updateAddressByEmail(String email, AddressDetails updatedAddress, String role);
    void deleteAddressByEmail(String email, String role);
    AddressDetails updateAddressById(Long addressId, AddressDetails updatedAddress, String role);
    void deleteAddressById(Long addressId, String role);
    AddressDetails getById(Long addressId, String role);
	AddressDetails createAddress(AddressDetails addressDetails, String role);

}
