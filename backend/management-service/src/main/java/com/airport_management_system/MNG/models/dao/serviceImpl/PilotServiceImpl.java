package com.airport_management_system.MNG.models.dao.serviceImpl;

import com.airport_management_system.MNG.models.dao.services.PilotService;
import com.airport_management_system.MNG.models.dto.AddressDetails;
import com.airport_management_system.MNG.models.dto.PlaneAllocationResponse;
import com.airport_management_system.MNG.models.dto.User;
import com.airport_management_system.MNG.models.pojo.Pilot;
import com.airport_management_system.MNG.models.repositories.PilotRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PilotServiceImpl implements PilotService {

    @Autowired
    private PilotRepository pilotRepository; 

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Pilot save(Pilot pilot, String token) {
        Pilot savedPilot = pilotRepository.save(pilot);

        if (savedPilot.getAddressId() != null) {
            try {
                savedPilot.setAddress(populateAddress(savedPilot.getAddressId(), token));
            } catch (Exception e) {
                System.err.println("Failed to fetch address for pilot " + savedPilot.getPilotId() + ": " + e.getMessage());
                savedPilot.setAddress(null);
            }
        }

        return savedPilot;
    }


    @Override
    public List<Pilot> findAll(String token) {
        List<Pilot> pilots = pilotRepository.findAll();
        for(Pilot p:pilots)
        {
        	p.setAddress(populateAddress(p.getAddressId(),token));
        }
        return pilots;
    }

    @Override
    public Optional<Pilot> findById(Long id, String token) {
    	return pilotRepository.findById(id).map(p -> {
    	    AddressDetails address = populateAddress(p.getAddressId(), token);
    	    p.setAddress(address);
    	    return p;
    	});

    }

    @Override
    public void deleteById(Long id) {
        // 1. Check if pilot exists
        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pilot not found with ID: " + id));

        // 2. Call Plane Allocation Service to check if pilot is assigned
        String allocationServiceUrl = "http://ManagementApp/api/plane-allocation/all"; // adjust if needed
        try {
            ResponseEntity<PlaneAllocationResponse[]> response =
                    restTemplate.getForEntity(allocationServiceUrl, PlaneAllocationResponse[].class);

            if (response.getBody() != null) {
                boolean hasActiveAllocation = Arrays.stream(response.getBody())
                        .anyMatch(a -> a.getPilotId().equals(id)
                                && (a.getToDate() == null || a.getToDate().isAfter(LocalDateTime.now())));

                if (hasActiveAllocation) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Cannot delete pilot '" + pilot.getName() +
                            "' because they are currently allocated to a plane."
                    );
                }
            }
        } catch (org.springframework.web.client.RestClientException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to check pilot allocations before deletion: " + e.getMessage()
            );
        }

        // 3. If no active allocation → delete
        pilotRepository.deleteById(id);
    }


    public AddressDetails populateAddress(Long addressId, String token) {
        if (addressId != null) {
            String userServiceUrl = "http://AuthenticationApp/api/address/getById/" + addressId;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);
                headers.set("Role","ADMIN");
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                
                ResponseEntity<AddressDetails> userResponse = restTemplate.exchange(
                    userServiceUrl,
                    HttpMethod.GET,
                    entity,
                    AddressDetails.class);
                
                AddressDetails user = userResponse.getBody();
                if (user != null) {
                    return user;
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch address for user: " + e.getMessage());
            }
        }
        return null;
    }


}