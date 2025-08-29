package com.airport_management_system.MNG.models.bao;

import com.airport_management_system.MNG.models.dao.services.HangarAllocationService;
import com.airport_management_system.MNG.models.dao.services.HangarService;
import com.airport_management_system.MNG.models.dto.HangarAllocationDTO;
import com.airport_management_system.MNG.models.pojo.Hangar;
import com.airport_management_system.MNG.models.pojo.HangarAllocation;
import com.airport_management_system.MNG.models.customExceptions.HangarUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.util.List;

@Component
public class HangarAllocationBAO {

    @Autowired
    private HangarAllocationService service;
    @Autowired
    private HangarService hangarService;  
    public HangarAllocation allocate(HangarAllocationDTO dto) {
        System.out.println("==> BAO allocate called with: " + dto);

        Hangar hangar = hangarService.getHangarById(dto.getHangarId())
            .orElseThrow(() -> new HangarUnavailableException("Hangar not found"));

   
        List<HangarAllocation> hangarConflicts = service.getAvailability(
            dto.getHangarId(),
            dto.getFromDate(),
            dto.getToDate()
        );
        if (hangarConflicts.size() >= hangar.getCapacity()) {
            throw new HangarUnavailableException(
                "Hangar is fully booked for the selected dates."
            );
        }

        
        List<HangarAllocation> planeConflicts = service.getPlaneAllocations(
            dto.getPlaneId(),
            dto.getFromDate(),
            dto.getToDate()
        );
        if (!planeConflicts.isEmpty()) {
            throw new HangarUnavailableException(
                "Plane is already allocated to another hangar during this period."
            );
        }

        HangarAllocation allocation = new HangarAllocation(
            dto.getPlaneId(),
            dto.getHangarId(),
            dto.getFromDate(),
            dto.getToDate(),
            dto.getUserId()
        );

        return service.allocateHangar(allocation);
    }



    public List<HangarAllocation> getAllAllocations() {
        return service.getAllAllocations();
    }
    public void deleteAllocation(Long planeId, Long hangarId, Timestamp fromDate) {
        service.deleteAllocation(planeId, hangarId, fromDate);
    }

    
    public List<HangarAllocation> getAllocationsByHangarId(Long hangarId) {
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return service.getAvailability(hangarId, now, Timestamp.valueOf("9999-12-31 23:59:59"));
    }

    public List<HangarAllocation> getAvailability(Long hangarId, Timestamp from, Timestamp to) {
        return service.getAvailability(hangarId, from, to);
    }



    public int getHangarCapacity(Long hangarId) {
        return hangarService.getHangarById(hangarId)
                .orElseThrow(() -> new HangarUnavailableException("Hangar not found"))
                .getCapacity();
    }

    
    
}
