package com.airport_management_system.MNG.models.dao.services;

import com.airport_management_system.MNG.models.pojo.PlaneOwner;

import java.util.List;
import java.util.Optional;

public interface PlaneOwnerService {
    PlaneOwner saveOwner(PlaneOwner owner, String token);
    List<PlaneOwner> findAll(String token,String role);
    Optional<PlaneOwner> getById(Long id, String token);
    PlaneOwner updateById(Long id, PlaneOwner updated, String token);
    void deleteById(Long id, String token);

    Optional<PlaneOwner> fetchByAddressEmail(String email, String token);
    PlaneOwner updateByAddressEmail(String email, PlaneOwner updated, String token);
    void deleteByAddressEmail(String email, String token);
    Optional<PlaneOwner> getOwnerByPlaneNumber(String planeNumber, String token);

    PlaneOwner getOwnerByEmail(String email, String token); 
    void deleteOwnerByEmail(String email, String token);   
}
