package com.airport_management_system.MNG.models.repositories;

import com.airport_management_system.MNG.models.pojo.Plane;
import com.airport_management_system.MNG.models.pojo.PlaneOwner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaneRepository extends JpaRepository<Plane, Long> {

    Plane save(Plane plane);

    List<Plane> findAll();

    Optional<Plane> findByPlaneNumber(String planeNumber);

    //@Query("SELECT p FROM Plane p JOIN p.owner o JOIN o.address a WHERE a.email = :email")
    //List<Plane> findByOwnerEmail(@Param("email") String email);

    
    Optional<Plane> findById(Long planeId);

    @Transactional
    void deleteById(Long planeId);
    Optional<PlaneOwner> findOwnerByPlaneNumber(@Param("planeNumber") String planeNumber);

    @Transactional
    void deleteByPlaneNumber(String planeNumber);
}
