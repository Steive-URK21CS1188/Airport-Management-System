package com.airport_management_system.MNG.models.dao.services;

import com.airport_management_system.MNG.models.pojo.Hangar;

import java.util.List;
import java.util.Optional;

public interface HangarService {

    // Basic CRUD
    Hangar addHangar(Hangar hangar);

    List<Hangar> getAllHangars();

    Optional<Hangar> getHangarById(Long id);

    Hangar updateHangar(Long id, Hangar updatedHangar);

    void deleteHangar(Long id);

    // Custom queries based on repository
    List<Hangar> getHangarsByUserId(Long userId);  

    Optional<Hangar> getHangarByHangarName(String hangarName);

    List<Hangar> getHangarsByHangarLocation(String hangarLocation);

    Optional<Hangar> getHangarByHangarNameAndHangarLocation(String hangarName, String hangarLocation);

    List<Hangar> getHangarsByCapacityGreaterThanEqual(int capacity);

}
