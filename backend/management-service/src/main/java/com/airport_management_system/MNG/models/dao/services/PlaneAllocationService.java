package com.airport_management_system.MNG.models.dao.services;

import com.airport_management_system.MNG.models.dto.PlaneAllocationRequest;
import com.airport_management_system.MNG.models.dto.PlaneAllocationResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface PlaneAllocationService {
    String allocatePlaneToPilot(PlaneAllocationRequest request);
    List<PlaneAllocationResponse> getAllocationsByManager(Long managerId);
    List<PlaneAllocationResponse> getAllAllocations();
    void deleteAllocation(Long planeId, Long pilotId, LocalDateTime fromDate);

}
