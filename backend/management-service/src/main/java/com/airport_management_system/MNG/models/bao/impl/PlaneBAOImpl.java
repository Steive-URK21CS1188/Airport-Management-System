package com.airport_management_system.MNG.models.bao.impl;

import com.airport_management_system.MNG.models.bao.PlaneBAO;
import com.airport_management_system.MNG.models.customExceptions.*;
import com.airport_management_system.MNG.models.dao.services.PlaneService;
import com.airport_management_system.MNG.models.pojo.Plane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaneBAOImpl implements PlaneBAO {

    @Autowired
    private PlaneService service;

    private void validateAccess(String role) {
        if (!("admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role))) {
            throw new UnauthorizedAccessException("Access denied.");
        }
    }


    @Override
    public Plane addPlane(Plane plane, String token, String role) {
        validateAccess(role);
        return service.savePlane(plane,token);
    }

    @Override
    public Plane getPlane(String planeNumber, String token, String role) {
        validateAccess(role);
        return service.getByPlaneNumber(planeNumber,token,role)
                .orElseThrow(() -> new ResourceNotFoundException("Plane not found."));
    }

    @Override
    public List<Plane> getAllPlanes(String token, String role) {
        validateAccess(role);
        return service.getAllPlanes(token,role);
    }

   
    
    @Override
    public Plane updatePlane(String planeNumber, Plane updatedPlane, String role) {
    	validateAccess(role);
        return service.updatePlane(planeNumber, updatedPlane);
    }

    @Override
    public Plane getById(Long planeId,String token, String role) {
        validateAccess(role);
        return service.getById(planeId,token,role)
            .orElseThrow(() -> new ResourceNotFoundException("Plane not found"));
    }

    @Override
    public Plane updateById(Long planeId, Plane updated,String token, String role) {
        validateAccess(role);
        return service.updateById(planeId, updated,token, role);
    }

    @Override
    public void deleteById(Long planeId, String role) {
        validateAccess(role);
        service.deleteById(planeId);
    }

    
    @Override
    public void deletePlane(String planeNumber, String role) {
        validateAccess(role);
        service.deleteByPlaneNumber(planeNumber);
    }
}
