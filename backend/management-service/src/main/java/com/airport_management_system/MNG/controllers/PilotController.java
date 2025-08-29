package com.airport_management_system.MNG.controllers;

import com.airport_management_system.MNG.models.bao.PilotBAO;
import com.airport_management_system.MNG.models.customExceptions.ResourceNotFoundException;
import com.airport_management_system.MNG.models.dto.PilotDTO;
import com.airport_management_system.MNG.models.pojo.Pilot;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pilots")
public class PilotController {

    @Autowired
    private PilotBAO pilotBAO;

    @PostMapping("/add")
    public Pilot addPilot(
            @Valid @RequestBody PilotDTO pilotDTO,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("Role") String role) {

        // Extract token from Authorization header: "Bearer <token>"
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            // handle missing or malformed token
            throw new RuntimeException("Invalid Authorization header");
        }

        return pilotBAO.addPilotFromDTO(pilotDTO, token, role);
    }

    @GetMapping("/getAll")
    public List<Pilot> getAllPilots(@RequestParam String token) {
        return pilotBAO.getAllPilots(token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id,@RequestParam String token) {
        Pilot pilot = pilotBAO.getPilotById(id,token)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found with id: " + id));
        return ResponseEntity.ok(pilot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pilot> updatePilot(@PathVariable Long id, @Valid @RequestBody PilotDTO pilotDTO, @RequestParam String token,@RequestHeader String role) {
        Pilot updatedPilot = pilotBAO.updatePilotFromDTO(id, pilotDTO,token,role);
        return ResponseEntity.ok(updatedPilot);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePilot(@PathVariable Long id,@RequestParam String token,@RequestHeader String role) {
        pilotBAO.deletePilotById(id);
        return ResponseEntity.ok().build();
    }
}
