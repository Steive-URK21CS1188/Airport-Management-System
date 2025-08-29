package com.airport_management_system.MNG.models.dao.services;

import com.airport_management_system.MNG.models.dto.AddressDetails;
import com.airport_management_system.MNG.models.pojo.Pilot;

import java.util.List;
import java.util.Optional;

public interface PilotService {
    //Pilot save(Pilot pilot);
    List<Pilot> findAll(String token);
    Optional<Pilot> findById(Long id, String token);
    void deleteById(Long id);
    AddressDetails populateAddress(Long addressId, String token);
	Pilot save(Pilot pilot, String token);
}
