package com.airport_management_system.AMS.models.dao.serviceImpl;

import com.airport_management_system.AMS.models.pojo.AddressDetails;
import com.airport_management_system.AMS.models.repositories.AddressRepository;
import com.airport_management_system.AMS.models.dao.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository repository;

    @Override
    public AddressDetails save(AddressDetails address) {
        return repository.save(address);
    }

    @Override
    public Optional<AddressDetails> getByEmail(String email) {
        return repository.findByEmail(email);
    }
    
    @Override
    public AddressDetails updateAddressByEmail(String email, AddressDetails updatedAddress) {
    	AddressDetails existing = repository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("Address not found with Mail: " + email));

	    existing.setHouseNo(updatedAddress.getHouseNo());
	    existing.setStreet(updatedAddress.getStreet());
	    existing.setCity(updatedAddress.getCity());
	    existing.setState(updatedAddress.getState());
	    existing.setPincode(updatedAddress.getPincode());
	    existing.setPhoneno(updatedAddress.getPhoneno());
	    existing.setEmail(updatedAddress.getEmail()); 

	    return repository.save(existing);
        //return repository.save(updatedAddress);
    }

    @Override
    public void deleteByEmail(String email) {
        repository.deleteByEmail(email);
    }

    @Override
    public Optional<AddressDetails> getById(Long addressId) {
    	System.out.println("From Address Service Impl");
        return repository.findById(addressId);
    }

    @Override
    public AddressDetails updateById(Long addressId, AddressDetails updatedAddress) {
    	 AddressDetails existing = repository.findById(addressId)
	        .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));

	    existing.setHouseNo(updatedAddress.getHouseNo());
	    existing.setStreet(updatedAddress.getStreet());
	    existing.setCity(updatedAddress.getCity());
	    existing.setState(updatedAddress.getState());
	    existing.setPincode(updatedAddress.getPincode());
	    existing.setPhoneno(updatedAddress.getPhoneno());
	    existing.setEmail(updatedAddress.getEmail()); 

	    return repository.save(existing);
        //return repository.save(updatedAddress);
    }

    @Override
    public void deleteById(Long addressId) {
        repository.deleteById(addressId);
    }

    
    @Override
    public List<AddressDetails> getAll() {
        return repository.findAll();
    }
}
