package com.airport_management_system.MNG.models.repositories;

import com.airport_management_system.MNG.models.pojo.Hangar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HangarRepository extends JpaRepository<Hangar, Long> {

    List<Hangar> findByUserId(Long userId);

    Optional<Hangar> findByHangarName(String hangarName);

    List<Hangar> findByHangarLocation(String hangarLocation);

    Optional<Hangar> findByHangarNameAndHangarLocation(String hangarName, String hangarLocation);

    List<Hangar> findByCapacityGreaterThanEqual(int capacity);
    
    @Transactional
    void deleteById(Long id);
}
