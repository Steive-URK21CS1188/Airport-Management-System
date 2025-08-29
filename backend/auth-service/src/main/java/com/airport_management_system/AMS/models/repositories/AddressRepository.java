package com.airport_management_system.AMS.models.repositories;

import com.airport_management_system.AMS.models.pojo.AddressDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressDetails, Long> {
	
    //AddressDetails save(AddressDetails address);
    
    List<AddressDetails> findAll();
    
    Optional<AddressDetails> findByEmail(String email);
    
    Optional<AddressDetails> findById(Long addressId);
    
    @Transactional
    void deleteByEmail(String email);
    
    @Transactional
    void deleteById(Long addressId);
}
