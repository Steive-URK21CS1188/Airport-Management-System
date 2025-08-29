package com.airport_management_system.MNG.controllers;

import com.airport_management_system.MNG.models.pojo.Hangar;
import com.airport_management_system.MNG.models.customExceptions.HangarNotFoundException;
import com.airport_management_system.MNG.models.customExceptions.DuplicateHangarException;
import com.airport_management_system.MNG.models.customExceptions.InvalidHangarException;
import com.airport_management_system.MNG.models.dao.services.HangarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hangars")
public class HangarController {

    @Autowired
    private HangarService hangarService;

    @PostMapping("/add")
    public ResponseEntity<String> addHangar(@Valid @RequestBody Hangar hangar) {
        if (hangar.getCapacity() < 2 || hangar.getCapacity() > 5) {
            throw new InvalidHangarException("Hangar capacity must be between 2 and 5.");
        }

        Optional<Hangar> existing = hangarService.getHangarByHangarNameAndHangarLocation(
                hangar.getHangarName(), hangar.getHangarLocation());

        if (existing.isPresent()) {
            throw new DuplicateHangarException("Hangar with the same name and location already exists.");
        }

        Hangar saved = hangarService.addHangar(hangar);
        return ResponseEntity.ok("Hangar with ID " + saved.getHangarId() + " added successfully.");
    }

    @GetMapping("/viewAll")
    public ResponseEntity<List<Hangar>> getAllHangars() {
        List<Hangar> list = hangarService.getAllHangars();
        if (list.isEmpty()) {
            throw new HangarNotFoundException("No hangars found in the system.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Hangar> getHangarById(@PathVariable Long id) {
        Hangar hangar = hangarService.getHangarById(id)
                .orElseThrow(() -> new HangarNotFoundException("Hangar with ID " + id + " not found."));
        return ResponseEntity.ok(hangar);
    }

    @GetMapping("/viewByName/{hangarName}")
    public ResponseEntity<Hangar> getHangarByHangarName(@PathVariable String hangarName) {
        Hangar hangar = hangarService.getHangarByHangarName(hangarName)
                .orElseThrow(() -> new HangarNotFoundException("Hangar with name '" + hangarName + "' not found."));
        return ResponseEntity.ok(hangar);
    }

    @GetMapping("/viewByNameAndLocation/{hangarName}/{hangarLocation}")
    public ResponseEntity<Hangar> getHangarByNameAndLocation(
            @PathVariable String hangarName,
            @PathVariable String hangarLocation) {
        Hangar hangar = hangarService.getHangarByHangarNameAndHangarLocation(hangarName, hangarLocation)
                .orElseThrow(() -> new HangarNotFoundException("Hangar with name '" + hangarName + "' and location '" + hangarLocation + "' not found."));
        return ResponseEntity.ok(hangar);
    }

    @GetMapping("/viewByUserId/{userId}")
    public ResponseEntity<List<Hangar>> getHangarsByUserId(@PathVariable Long userId) {
        List<Hangar> list = hangarService.getHangarsByUserId(userId);
        if (list.isEmpty()) {
            throw new HangarNotFoundException("No hangars found for user ID " + userId);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/viewByLocation/{hangarLocation}")
    public ResponseEntity<List<Hangar>> getHangarsByLocation(@PathVariable String hangarLocation) {
        List<Hangar> list = hangarService.getHangarsByHangarLocation(hangarLocation);
        if (list.isEmpty()) {
            throw new HangarNotFoundException("No hangars found at location '" + hangarLocation + "'");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/viewByMinCapacity/{capacity}")
    public ResponseEntity<List<Hangar>> getHangarsByMinCapacity(@PathVariable int capacity) {
        List<Hangar> list = hangarService.getHangarsByCapacityGreaterThanEqual(capacity);
        if (list.isEmpty()) {
            throw new HangarNotFoundException("No hangars found with capacity ≥ " + capacity);
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateHangar(@PathVariable Long id, @Valid @RequestBody Hangar hangar) {
        if (hangar.getCapacity() < 2 || hangar.getCapacity() > 5) {
            throw new InvalidHangarException("Hangar capacity must be between 2 and 5.");
        }

        Hangar updated = hangarService.updateHangar(id, hangar);
        if (updated == null) {
            throw new HangarNotFoundException("Cannot update. Hangar with ID " + id + " not found.");
        }
        return ResponseEntity.ok("Hangar with ID " + id + " updated successfully.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteHangar(@PathVariable Long id) {
        if (hangarService.getHangarById(id).isEmpty()) {
            throw new HangarNotFoundException("Cannot delete. Hangar with ID " + id + " not found.");
        }
        hangarService.deleteHangar(id);
        return ResponseEntity.ok("Hangar with ID " + id + " deleted successfully.");
    }
}
