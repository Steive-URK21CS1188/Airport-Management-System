package com.airport_management_system.MNG.controllers;

import com.airport_management_system.MNG.models.dto.HangarAllocationDTO;
import com.airport_management_system.MNG.models.pojo.HangarAllocation;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.airport_management_system.MNG.models.bao.HangarAllocationBAO;
import com.airport_management_system.MNG.models.customExceptions.*;
import com.airport_management_system.MNG.models.dao.services.HangarAllocationService;

import java.sql.Timestamp;
import java.util.List;
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hangar-allocation")
public class HangarAllocationController {

    @Autowired
    private HangarAllocationBAO bao;
    

    @GetMapping("/availability")
    public String checkAvailability(
            @RequestParam Long hangarId,
            @RequestParam String from,
            @RequestParam String to) {
        from = from.replace("T", " ");
        to = to.replace("T", " ");
        Timestamp fromTime = Timestamp.valueOf(from);
        Timestamp toTime = Timestamp.valueOf(to);
        try {
            
            int capacity = bao.getHangarCapacity(hangarId);

         
            List<HangarAllocation> allocations = bao.getAvailability(hangarId, fromTime, toTime);

            if (allocations.size() >= capacity) {
                return "Hangar is not available";
            } else {
                int remaining = capacity - allocations.size();
                return "Hangar is available (" + remaining + " slots left)";
            }
        } catch (HangarUnavailableException e) {
            return "Hangar is not available";
        }
    }


    @PostMapping("/allocate")
    public ResponseEntity<String> allocate(@RequestBody HangarAllocationDTO dto) {
        try {
            bao.allocate(dto);
            return ResponseEntity.ok("Hangar allocated successfully");
        } catch (HangarUnavailableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<HangarAllocation>> getAllAllocations() {
        List<HangarAllocation> allocations = bao.getAllAllocations();
        return ResponseEntity.ok(allocations);
    }

    @DeleteMapping("/delete/{planeId}/{hangarId}/{fromDate}")
    public ResponseEntity<String> deleteAllocation(
            @PathVariable Long planeId,
            @PathVariable Long hangarId,
            @PathVariable String fromDate) {
        try {
            
            Timestamp fromTimestamp = Timestamp.valueOf(fromDate.replace("T", " "));
            
            bao.deleteAllocation(planeId, hangarId, fromTimestamp);
            return ResponseEntity.ok("Hangar allocation deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




}
