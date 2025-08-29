package com.airport_management_system.MNG.models.bao.impl;

import com.airport_management_system.MNG.models.bao.PlaneOwnerBAO;
import com.airport_management_system.MNG.models.customExceptions.*;
import com.airport_management_system.MNG.models.dao.services.PlaneOwnerService;
import com.airport_management_system.MNG.models.pojo.PlaneOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class PlaneOwnerBAOImpl implements PlaneOwnerBAO {

    @Autowired
    private PlaneOwnerService service;

    private void validateAccess(String role) {
        if (!("admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role))) {
            throw new UnauthorizedAccessException("Access denied.");
        }
    }


    @Override
    public Optional<PlaneOwner> getOwnerByPlaneNumber(String planeNumber, String token, String role) {
        validateAccess(role);
        return service.getOwnerByPlaneNumber(planeNumber, token);
    }

    @Override
    public PlaneOwner addOwner(PlaneOwner owner, String token, String role) {
        validateAccess(role);
        return service.saveOwner(owner, token);
    }

    @Override
    public List<PlaneOwner> getAll(String token, String role) {
        validateAccess(role);
        return service.findAll(token,role);
    }

    @Override
    public PlaneOwner getById(Long ownerId, String token, String role) {
        validateAccess(role);
        return service.getById(ownerId, token)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
    }

    @Override
    public PlaneOwner updateById(Long ownerId, PlaneOwner updated, String token, String role) {
        validateAccess(role);
        return service.updateById(ownerId, updated, token);
    }

    @Override
    public String deleteById(Long ownerId, String token, String role) {
        validateAccess(role);
        service.deleteById(ownerId, token);
        return "Owner deleted successfully";
    }

    @Override
    public Optional<PlaneOwner> fetchByAddressEmail(String email, String token, String role) {
        validateAccess(role);
        return service.fetchByAddressEmail(email, token);
    }

    @Override
    public PlaneOwner updateByAddressEmail(String email, PlaneOwner updated, String token, String role) {
        validateAccess(role);
        return service.updateByAddressEmail(email, updated, token);
    }

    @Override
    public void deleteByAddressEmail(String email, String token, String role) {
        validateAccess(role);
        service.deleteByAddressEmail(email, token);
    }

    @Override
    public PlaneOwner getOwnerByEmail(String email, String token, String role) {
        validateAccess(role);
        return service.getOwnerByEmail(email, token);
    }

    @Override
    public void deleteOwnerByEmail(String email, String token, String role) {
        validateAccess(role);
        service.deleteOwnerByEmail(email, token);
    }
}
