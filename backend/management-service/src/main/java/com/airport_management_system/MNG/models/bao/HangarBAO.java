package com.airport_management_system.MNG.models.bao;

import com.airport_management_system.MNG.models.dao.services.HangarService;
import com.airport_management_system.MNG.models.pojo.Hangar;
import com.airport_management_system.MNG.models.pojo.HangarAllocation;
import com.airport_management_system.MNG.models.customExceptions.HangarAllocatedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HangarBAO {

    @Autowired
    private HangarService hangarService;

    @Autowired
    private HangarAllocationBAO hangarAllocationBAO; // Inject allocation BAO

    public Hangar addHangar(Hangar hangar) {
        return hangarService.addHangar(hangar);
    }

    public List<Hangar> getAllHangars() {
        return hangarService.getAllHangars();
    }

    public Optional<Hangar> getHangarById(Long id) {
        return hangarService.getHangarById(id);
    }

    public Hangar updateHangar(Long id, Hangar updatedHangar) {
        return hangarService.updateHangar(id, updatedHangar);
    }

    public void deleteHangar(Long id) {
        // 1. Check if the hangar has any active allocations
        List<HangarAllocation> activeAllocations = hangarAllocationBAO.getAllocationsByHangarId(id);
        if (!activeAllocations.isEmpty()) {
            throw new HangarAllocatedException("Cannot delete hangar: it is currently allocated to a plane.");
        }

        // 2. Proceed with deletion
        hangarService.deleteHangar(id);
    }

    public List<Hangar> getHangarsByUserId(Long userId) {
        return hangarService.getHangarsByUserId(userId);
    }

    public Optional<Hangar> getHangarByHangarName(String hangarName) {
        return hangarService.getHangarByHangarName(hangarName);
    }

    public List<Hangar> getHangarsByHangarLocation(String hangarLocation) {
        return hangarService.getHangarsByHangarLocation(hangarLocation);
    }

    public Optional<Hangar> getHangarByHangarNameAndHangarLocation(String hangarName, String hangarLocation) {
        return hangarService.getHangarByHangarNameAndHangarLocation(hangarName, hangarLocation);
    }

    public List<Hangar> getHangarsByCapacityGreaterThanEqual(int capacity) {
        return hangarService.getHangarsByCapacityGreaterThanEqual(capacity);
    }
}
