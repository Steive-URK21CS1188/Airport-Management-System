package com.airport_management_system.MNG.models.dao.services;

import com.airport_management_system.MNG.models.pojo.Plane;

import java.util.List;
import java.util.Optional;

public interface PlaneService {
    Plane savePlane(Plane plane, String token);
    List<Plane> getAllPlanes(String token, String role);
    //List<Plane> getByOwnerEmail(String email);
    Plane updatePlane(String planeNumber, Plane updatedPlane);
    void deleteByPlaneNumber(String planeNumber);
    Optional<Plane> getById(Long planeId,String token, String role);
    Plane updateById(Long planeId, Plane updatedPlane, String token, String role);
    void deleteById(Long planeId);
	Optional<Plane> getByPlaneNumber(String planeNumber, String token, String role);

}
