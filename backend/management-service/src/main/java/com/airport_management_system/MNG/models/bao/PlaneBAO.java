package com.airport_management_system.MNG.models.bao;

import com.airport_management_system.MNG.models.pojo.Plane;

import java.util.List;

public interface PlaneBAO {
    Plane addPlane(Plane plane, String token, String role);
    List<Plane> getAllPlanes(String token, String role);
    //List<Plane> getPlanesByOwnerEmail(String email, String role);
    Plane updatePlane(String planeNumber, Plane updatedPlane, String role);
    void deletePlane(String planeNumber, String role);
    Plane getById(Long planeId,String token, String role);
    Plane updateById(Long planeId, Plane updated,String token, String role);
    void deleteById(Long planeId, String role);
	Plane getPlane(String planeNumber, String token, String role);

}

