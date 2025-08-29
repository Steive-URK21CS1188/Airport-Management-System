package com.airport_management_system.MNG.models.repositories;

import com.airport_management_system.MNG.models.pojo.Plane;
import com.airport_management_system.MNG.models.pojo.PlaneOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaneOwnerRepository extends JpaRepository<PlaneOwner, Long> {

    List<PlaneOwner> findAll();

    Optional<PlaneOwner> findByAddressId(Long addressId);


    // This is the corrected method using a custom query.
    @Query("SELECT po FROM PlaneOwner po JOIN po.planes p WHERE p.planeNumber = :planeNumber")
    Optional<PlaneOwner> findByPlaneNumber(@Param("planeNumber") String planeNumber);
    @Modifying
    @Transactional
    void deleteByAddressId(Long addressId);


}