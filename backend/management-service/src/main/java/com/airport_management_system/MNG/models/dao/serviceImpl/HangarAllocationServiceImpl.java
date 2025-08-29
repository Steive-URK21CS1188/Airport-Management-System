package com.airport_management_system.MNG.models.dao.serviceImpl;

import com.airport_management_system.MNG.models.customExceptions.HangarUnavailableException;
import com.airport_management_system.MNG.models.customExceptions.ResourceNotFoundException;
import com.airport_management_system.MNG.models.dao.services.HangarAllocationService;
import com.airport_management_system.MNG.models.dto.HangarAllocationDTO;
import com.airport_management_system.MNG.models.pojo.Hangar;
import com.airport_management_system.MNG.models.pojo.HangarAllocation;
import com.airport_management_system.MNG.models.pojo.HangarAllocationId;
import com.airport_management_system.MNG.models.repositories.HangarAllocationRepository;
import com.airport_management_system.MNG.models.repositories.HangarRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class HangarAllocationServiceImpl implements HangarAllocationService {

    @Autowired
    private HangarAllocationRepository repository;
    @Autowired
    private HangarRepository hangarRepository;
    
    @Override
    public HangarAllocation allocateHangar(HangarAllocation allocation) {
        return repository.save(allocation);
    }
    public List<HangarAllocation> getAllAllocations() {
        return repository.findAll();
    }

    @Override
    public void deleteAllocation(Long planeId, Long hangarId, Timestamp fromDate) {
        HangarAllocationId allocationId = new HangarAllocationId(planeId, hangarId, fromDate);
        
        HangarAllocation allocation = repository.findById(allocationId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Hangar allocation not found with planeId: " + planeId 
                + ", hangarId: " + hangarId 
                + ", fromDate: " + fromDate
            ));

        repository.delete(allocation);
    }

    public List<HangarAllocation> getPlaneAllocations(Long planeId, Timestamp from, Timestamp to) {
        return repository.findByPlaneIdAndFromDateLessThanAndToDateGreaterThan(
            planeId, to, from
        );
    }



    @Override
    public List<HangarAllocation> getAvailability(Long hangarId, Timestamp from, Timestamp to) {
        boolean exists = hangarRepository.existsById(hangarId);
        if (!exists) {
            throw new HangarUnavailableException("Hangar not found");
        }

        // Just return overlapping allocations
        return repository.findByHangarIdAndFromDateLessThanAndToDateGreaterThan(hangarId, to, from);
    }



}
