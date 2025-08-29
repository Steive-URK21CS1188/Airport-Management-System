package com.airport_management_system.AMS.models.bao.impl;

import com.airport_management_system.AMS.models.bao.AddressBAO;
import com.airport_management_system.AMS.models.customExceptions.*;
import com.airport_management_system.AMS.models.dao.services.AddressService;
import com.airport_management_system.AMS.models.pojo.AddressDetails;
import com.airport_management_system.AMS.models.repositories.AddressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBAOImpl implements AddressBAO {

    @Autowired
    private AddressService service;
    @Autowired
    private AddressRepository addressRepository;
    public AddressDetails createAddress(AddressDetails addressDetails, String role) {
        // Optional: Validate role and/or addressDetails here
    	validateAdmin(role); 
        // Save the address entity using your DAO/repository
        return addressRepository.save(addressDetails);
    }

    private void validateAdmin(String role) {
        if (!"admin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("Only admin can access this resource");
        }
    }

    @Override
    public AddressDetails addAddress(AddressDetails address, String role) {
        validateAdmin(role);
        return service.save(address);
    }

    @Override
    public AddressDetails getByEmail(String email, String role) {
        validateAdmin(role);
        return service.getByEmail(email).orElseThrow();
    }

    @Override
    public List<AddressDetails> getAll(String role) {
        validateAdmin(role);
        return service.getAll();
    }
    
    @Override
    public AddressDetails updateAddressByEmail(String email, AddressDetails updatedAddress, String role) {
    	validateAdmin(role);
        return service.updateAddressByEmail(email, updatedAddress);
    }

    @Override
    public AddressDetails getById(Long addressId, String role) {
        validateAdmin(role);
        return service.getById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    @Override
    public AddressDetails updateAddressById(Long addressId, AddressDetails updatedAddress, String role) {
        validateAdmin(role);
        return service.updateById(addressId, updatedAddress);
    }

    @Override
    public void deleteAddressById(Long addressId, String role) {
        validateAdmin(role);
        service.deleteById(addressId);
    }

    
    @Override
    public void deleteAddressByEmail(String email, String role) {
    	validateAdmin(role);
        service.deleteByEmail(email);
    }

}
