package com.airport_management_system.MNG.models.dao.serviceImpl;

import com.airport_management_system.MNG.models.pojo.*;
import com.airport_management_system.MNG.models.repositories.*;
import com.airport_management_system.MNG.models.customExceptions.*;
import com.airport_management_system.MNG.models.dao.services.PlaneService;
import com.airport_management_system.MNG.models.dto.AddressDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PlaneServiceImpl implements PlaneService {

    private static final String AUTH_SERVICE_BASE_URL = "http://AuthenticationApp/api";

    @Autowired
    private PlaneRepository planeRepository;

    @Autowired
    private PlaneOwnerRepository planeOwnerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Plane savePlane(Plane plane, String token) {
        handlePlaneOwner(plane,token);
        // Removed handlePlaneUser(plane);
        return planeRepository.save(plane);
    }

    private void handlePlaneOwner(Plane plane, String token) {
        PlaneOwner owner = plane.getOwner();
        if (owner != null) {
            AddressDetails address = owner.getAddress();

            if (address != null) {
                // Save address in auth-service first
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);
                headers.set("Role", "ADMIN");

                HttpEntity<AddressDetails> request = new HttpEntity<>(address, headers);

                ResponseEntity<AddressDetails> response = restTemplate.exchange(
                        "http://AuthenticationApp/api/address",
                        HttpMethod.POST,
                        request,
                        AddressDetails.class
                );

                AddressDetails savedAddress = response.getBody();
                if (savedAddress == null || savedAddress.getAddressId() == null) {
                    throw new RuntimeException("Failed to save address in auth-service");
                }

                owner.setAddressId(savedAddress.getAddressId()); // ✅ sets FK
            }

            // Now save the PlaneOwner in management DB
            PlaneOwner savedOwner = planeOwnerRepository.save(owner);
            plane.setOwner(savedOwner);
        }
    }


    private AddressDetails getAddressByEmail(String email) {
        String url = AUTH_SERVICE_BASE_URL + "/address/email/" + email;
        return restTemplate.getForObject(url, AddressDetails.class);
    }

    // Removed handlePlaneUser and getUserById methods

    @Override
    public Optional<Plane> getByPlaneNumber(String planeNumber, String token, String role) {
        return planeRepository.findByPlaneNumber(planeNumber).map(plane -> {
            PlaneOwner owner = plane.getOwner();
            if (owner != null) {
                owner.setAddress(fetchAddress(owner.getAddressId(), token, role));
            }
            return plane;
        });
    }

    @Override
    public List<Plane> getAllPlanes(String token, String role) {
        List<Plane> planes = planeRepository.findAll();

        for (Plane plane : planes) {
            PlaneOwner owner = plane.getOwner();
            if (owner != null) {
                owner.setAddress(fetchAddress(owner.getAddressId(), token, role));
            }
        }
        return planes;
    }


    @Override
    public Plane updatePlane(String planeNumber, Plane updatedPlane) {
        Plane existing = planeRepository.findByPlaneNumber(planeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Plane not found: " + planeNumber));

        // Only update plane fields, not owner/address
        existing.setPlaneNumber(updatedPlane.getPlaneNumber());
        existing.setModel(updatedPlane.getModel());
        existing.setCapacity(updatedPlane.getCapacity());

        return planeRepository.save(existing);
    }
    @Override
    public Optional<Plane> getById(Long planeId, String token, String role) {
        Optional<Plane> planeOpt = planeRepository.findById(planeId);

        planeOpt.ifPresent(plane -> {
            PlaneOwner owner = plane.getOwner();
            if (owner != null && owner.getAddressId() != null) {
                owner.setAddress(fetchAddress(owner.getAddressId(), token, role));
            }
        });

        return planeOpt;
    }

    @Override
    public Plane updateById(Long planeId, Plane updatedPlane, String token, String role) {
        Plane existing = planeRepository.findById(planeId)
                .orElseThrow(() -> new ResourceNotFoundException("Plane not found: " + planeId));

        // Only update plane fields, not owner/address
        existing.setPlaneNumber(updatedPlane.getPlaneNumber());
        existing.setModel(updatedPlane.getModel());
        existing.setCapacity(updatedPlane.getCapacity());

        return planeRepository.save(existing);
    }


    private void updatePlaneFields(Plane existing, Plane updated) {
        existing.setPlaneNumber(updated.getPlaneNumber());
        existing.setModel(updated.getModel());
        existing.setCapacity(updated.getCapacity());

    }


    @Override
    public void deleteById(Long planeId) {
        planeRepository.deleteById(planeId);
    }

    @Override
    public void deleteByPlaneNumber(String planeNumber) {
        planeRepository.deleteByPlaneNumber(planeNumber);
    }
    
    private AddressDetails fetchAddress(Long addressId, String token, String role) {
        if (addressId == null) return null;

        String addressServiceBaseUrl = "http://AuthenticationApp/api/address/getById/" + addressId;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Role", role);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<AddressDetails> response = restTemplate.exchange(
                    addressServiceBaseUrl,
                    HttpMethod.GET,
                    entity,
                    AddressDetails.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch address for addressId: " + addressId);
            e.printStackTrace();
        }
        return null;
    }

}
