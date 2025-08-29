package com.airport_management_system.MNG.models.dao.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airport_management_system.MNG.models.customExceptions.DuplicateHangarException;
import com.airport_management_system.MNG.models.customExceptions.HangarNotFoundException;
import com.airport_management_system.MNG.models.customExceptions.InvalidHangarException;
import com.airport_management_system.MNG.models.dao.services.HangarService;
import com.airport_management_system.MNG.models.pojo.Hangar;
import com.airport_management_system.MNG.models.repositories.HangarRepository;

@Service
public class HangarServiceImpl implements HangarService {

    @Autowired
    private HangarRepository hangarRepository;

    @Override
    public Hangar addHangar(Hangar hangar) {
        validateHangar(hangar);

        Optional<Hangar> existing = hangarRepository.findByHangarNameAndHangarLocation(
                hangar.getHangarName(), hangar.getHangarLocation());

        if (existing.isPresent()) {
            throw new DuplicateHangarException("Hangar with the same name and location already exists.");
        }

        return hangarRepository.save(hangar);
    }

    @Override
    public List<Hangar> getAllHangars() {
        return hangarRepository.findAll();
    }

    @Override
    public Optional<Hangar> getHangarById(Long id) {
        return hangarRepository.findById(id);
    }

    @Override
    public Hangar updateHangar(Long id, Hangar updatedHangar) {
        validateHangar(updatedHangar);

        return hangarRepository.findById(id).map(existingHangar -> {
            existingHangar.setHangarName(updatedHangar.getHangarName());
            existingHangar.setCapacity(updatedHangar.getCapacity());
            existingHangar.setHangarLocation(updatedHangar.getHangarLocation());
            existingHangar.setUserId(updatedHangar.getUserId());
            return hangarRepository.save(existingHangar);
        }).orElseThrow(() -> new HangarNotFoundException("Cannot update. Hangar with ID " + id + " not found."));
    }

    @Override
    public void deleteHangar(Long id) {
        if (!hangarRepository.existsById(id)) {
            throw new HangarNotFoundException("Cannot delete. Hangar with ID " + id + " not found.");
        }
        hangarRepository.deleteById(id);
    }

    @Override
    public List<Hangar> getHangarsByUserId(Long userId) {
        return hangarRepository.findByUserId(userId);
    }

    @Override
    public Optional<Hangar> getHangarByHangarName(String hangarName) {
        return hangarRepository.findByHangarName(hangarName);
    }

    @Override
    public List<Hangar> getHangarsByHangarLocation(String hangarLocation) {
        return hangarRepository.findByHangarLocation(hangarLocation);
    }

    @Override
    public Optional<Hangar> getHangarByHangarNameAndHangarLocation(String hangarName, String hangarLocation) {
        return hangarRepository.findByHangarNameAndHangarLocation(hangarName, hangarLocation);
    }

    @Override
    public List<Hangar> getHangarsByCapacityGreaterThanEqual(int capacity) {
        return hangarRepository.findByCapacityGreaterThanEqual(capacity);
    }

    //validation method used in add and update
    private void validateHangar(Hangar hangar) {
        if (hangar == null ||
            hangar.getHangarName() == null || hangar.getHangarName().trim().isEmpty() ||
            hangar.getHangarLocation() == null || hangar.getHangarLocation().trim().isEmpty() ||
            hangar.getUserId() == null ||
            hangar.getCapacity() < 2 || hangar.getCapacity() > 5) {
            throw new InvalidHangarException("Hangar details are invalid. Ensure name, location, userId are present and capacity is between 2 and 5.");
        }
    }
}
