package com.airport_management_system.AMS.models.dao.services;

import com.airport_management_system.AMS.models.pojo.AddressDetails;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    AddressDetails save(AddressDetails address);
    Optional<AddressDetails> getByEmail(String email);
    List<AddressDetails> getAll();
    AddressDetails updateAddressByEmail(String email, AddressDetails updatedAddress);
    void deleteByEmail(String email);
    Optional<AddressDetails> getById(Long addressId);
    AddressDetails updateById(Long addressId, AddressDetails updatedAddress);
    void deleteById(Long addressId);

}
