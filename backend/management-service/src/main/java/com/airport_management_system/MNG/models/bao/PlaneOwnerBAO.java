package com.airport_management_system.MNG.models.bao;

import com.airport_management_system.MNG.models.pojo.PlaneOwner;
import java.util.Optional;
import java.util.List;

public interface PlaneOwnerBAO {
    PlaneOwner addOwner(PlaneOwner owner, String token, String role);
    List<PlaneOwner> getAll(String token, String role);
    Optional<PlaneOwner> getOwnerByPlaneNumber(String planeNumber, String token, String role);
    PlaneOwner getById(Long ownerId, String token, String role);
    PlaneOwner updateById(Long ownerId, PlaneOwner updated, String token, String role);
    String deleteById(Long ownerId, String token, String role);
    PlaneOwner updateByAddressEmail(String email, PlaneOwner updated, String token, String role);
    void deleteByAddressEmail(String email, String token, String role);
    Optional<PlaneOwner> fetchByAddressEmail(String email, String token, String role);
    PlaneOwner getOwnerByEmail(String email, String token, String role);
    void deleteOwnerByEmail(String email, String token, String role);
}
