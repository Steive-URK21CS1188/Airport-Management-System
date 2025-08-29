package com.airport_management_system.MNG.models.dao.serviceImpl;

import com.airport_management_system.MNG.models.dto.PlaneAllocationRequest;
import com.airport_management_system.MNG.models.dto.PlaneAllocationResponse;
import com.airport_management_system.MNG.models.customExceptions.PilotUnavailableException;
import com.airport_management_system.MNG.models.customExceptions.PlaneUnavailableException;
import com.airport_management_system.MNG.models.pojo.PlaneAllocation;
import com.airport_management_system.MNG.models.pojo.PlaneAllocationId;
import com.airport_management_system.MNG.models.repositories.PlaneAllocationRepository;
import com.airport_management_system.MNG.models.dao.services.PlaneAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaneAllocationServiceImpl implements PlaneAllocationService {

    @Autowired
    private PlaneAllocationRepository repo;

    @Override
    public String allocatePlaneToPilot(PlaneAllocationRequest request) {

        // Check date validity
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new IllegalArgumentException("fromDate must be before toDate");
        }

        // Check if pilot is already allocated
        boolean isPilotBusy = repo.existsOverlappingAllocation(
            request.getPilotId(), request.getFromDate(), request.getToDate()
        );
        if (isPilotBusy) {
            throw new PilotUnavailableException("Pilot is already allocated in the given time.");
        }

        // Check if plane is already allocated
        boolean isPlaneBusy = repo.existsOverlappingAllocationForPlane(
            request.getPlaneId(), request.getFromDate(), request.getToDate()
        );
        if (isPlaneBusy) {
            throw new PlaneUnavailableException("Plane is already allocated in the given time.");
        }

        // Save allocation
        PlaneAllocation allocation = new PlaneAllocation(
            request.getPlaneId(),
            request.getPilotId(),
            request.getFromDate(),
            request.getToDate(),
            request.getManagerUserId()
        );

        repo.save(allocation);

        return "Plane successfully allocated to pilot!";
    }



    @Override
    public List<PlaneAllocationResponse> getAllocationsByManager(Long managerId) {
        return repo.findByUserId(managerId)
                .stream()
                .map(a -> new PlaneAllocationResponse(
                        a.getPlaneId(), a.getPilotId(),
                        a.getFromDate(), a.getToDate(), a.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaneAllocationResponse> getAllAllocations() {
        return repo.findAll()
                .stream()
                .map(a -> new PlaneAllocationResponse(
                        a.getPlaneId(), a.getPilotId(),
                        a.getFromDate(), a.getToDate(), a.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllocation(Long planeId, Long pilotId, LocalDateTime fromDate) {
        PlaneAllocationId id = new PlaneAllocationId(planeId, pilotId, fromDate);
        PlaneAllocation allocation = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));
        repo.delete(allocation);
    }
}
