package com.airport_management_system.MNG.controllers;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.airport_management_system.MNG.models.dto.PlaneAllocationRequest;
import com.airport_management_system.MNG.models.dto.PlaneAllocationResponse;
import com.airport_management_system.MNG.models.dao.services.PlaneAllocationService;

@RestController
@RequestMapping("/api/plane-allocation")
public class PlaneAllocationController {

    @Autowired
    private PlaneAllocationService service;

    @PostMapping("/allocate")
    public ResponseEntity<String> allocate(@Valid @RequestBody PlaneAllocationRequest request) {
        return ResponseEntity.ok(service.allocatePlaneToPilot(request));
    }

    @GetMapping("/manager/{managerId}")
    public List<PlaneAllocationResponse> getAllocationsByManager(@PathVariable Long managerId) {
        return service.getAllocationsByManager(managerId);
    }

    @GetMapping("/all")
    public List<PlaneAllocationResponse> getAllAllocations() {
        return service.getAllAllocations();
    }

    @DeleteMapping("/{planeId}/{pilotId}/{fromDate}")
    public ResponseEntity<String> deleteAllocation(
            @PathVariable Long planeId,
            @PathVariable Long pilotId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {

        service.deleteAllocation(planeId, pilotId, fromDate);
        return ResponseEntity.ok("Allocation deleted successfully");
    }
}
