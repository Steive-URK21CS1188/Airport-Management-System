package com.airport_management_system.MNG.models.dao.serviceImpl;

import com.airport_management_system.MNG.models.dao.services.PlaneOwnerService;
import com.airport_management_system.MNG.models.dto.AddressDetails;
import com.airport_management_system.MNG.models.pojo.Pilot;
import com.airport_management_system.MNG.models.pojo.PlaneOwner;
import com.airport_management_system.MNG.models.repositories.PlaneOwnerRepository;
import com.airport_management_system.MNG.models.repositories.PlaneRepository;

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
public class PlaneOwnerServiceImpl implements PlaneOwnerService {

    @Autowired
    private PlaneOwnerRepository planeOwnerRepository;
    @Autowired
    private PlaneRepository planeRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String AUTH_SERVICE_URL = "http://AuthenticationApp/api/address";

    @Override
    public PlaneOwner saveOwner(PlaneOwner owner, String token) {
        PlaneOwner savedOwner = planeOwnerRepository.save(owner);

        if (savedOwner.getAddressId() != null) {
            try {
                savedOwner.setAddress(populateAddress(savedOwner.getAddressId(), token));
            } catch (Exception e) {
                System.err.println("Failed to fetch address for owner " + savedOwner.getOwnerId() + ": " + e.getMessage());
                savedOwner.setAddress(null);
            }
        }
        return savedOwner;
    }

    public AddressDetails populateAddress(Long addressId, String token) {
        if (addressId != null) {
            String addressServiceUrl = AUTH_SERVICE_URL + "/getById/" + addressId;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);
                headers.set("Role", "ADMIN");
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<AddressDetails> response = restTemplate.exchange(
                    addressServiceUrl,
                    HttpMethod.GET,
                    entity,
                    AddressDetails.class
                );

                return response.getBody();
            } catch (Exception e) {
                System.err.println("Failed to fetch address for owner: " + e.getMessage());
            }
        }
        return null;
    }

    // Update all methods below to accept String token param and use it in auth service calls

    @Override
    public Optional<PlaneOwner> getById(Long id, String token) {
        Optional<PlaneOwner> ownerOpt = planeOwnerRepository.findById(id);
        ownerOpt.ifPresent(owner -> enrichWithAddress(owner, token));
        return ownerOpt;
    }
    @Override
    public List<PlaneOwner> findAll(String token,String role) {
        List<PlaneOwner> owners = planeOwnerRepository.findAll();
        for (PlaneOwner o : owners) {
            enrichWithAddress(o, token);  
        }
        return owners;
    }

    
    @Override
    public PlaneOwner updateById(Long id, PlaneOwner updated, String token) {
        PlaneOwner existing = planeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + id));

        existing.setName(updated.getName());
        if (updated.getAddress() != null) {
            updateAddressInAuthService(existing.getAddressId(), updated.getAddress(), token);
        }
        return planeOwnerRepository.save(existing);
    }

    @Override
    public void deleteById(Long id, String token) {
        planeOwnerRepository.deleteById(id);
    }

    @Override
    public Optional<PlaneOwner> fetchByAddressEmail(String email, String token) {
        Long addressId = getAddressIdByEmail(email, token);
        Optional<PlaneOwner> ownerOpt = planeOwnerRepository.findByAddressId(addressId);
        ownerOpt.ifPresent(owner -> enrichWithAddress(owner, token));
        return ownerOpt;
    }

    @Override
    public PlaneOwner updateByAddressEmail(String email, PlaneOwner updated, String token) {
        Long addressId = getAddressIdByEmail(email, token);
        PlaneOwner existing = planeOwnerRepository.findByAddressId(addressId)
                .orElseThrow(() -> new RuntimeException("Owner not found with address email: " + email));

        existing.setName(updated.getName());
        if (updated.getAddress() != null) {
            updateAddressInAuthService(addressId, updated.getAddress(), token);
        }
        return planeOwnerRepository.save(existing);
    }

    @Override
    public void deleteByAddressEmail(String email, String token) {
        Long addressId = getAddressIdByEmail(email, token);
        planeOwnerRepository.deleteByAddressId(addressId);
    }

    @Override
    public Optional<PlaneOwner> getOwnerByPlaneNumber(String planeNumber, String token) {
        Optional<PlaneOwner> ownerOpt = planeOwnerRepository.findByPlaneNumber(planeNumber);
        ownerOpt.ifPresent(owner -> enrichWithAddress(owner, token));
        return ownerOpt;
    }

    @Override
    public PlaneOwner getOwnerByEmail(String email, String token) {
        Long addressId = getAddressIdByEmail(email, token);
        PlaneOwner owner = planeOwnerRepository.findByAddressId(addressId)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + email));
        enrichWithAddress(owner, token);
        return owner;
    }

    @Override
    public void deleteOwnerByEmail(String email, String token) {
        Long addressId = getAddressIdByEmail(email, token);
        planeOwnerRepository.deleteByAddressId(addressId);
    }

    // Helper method to update address in auth-service with token header
    private void updateAddressInAuthService(Long addressId, AddressDetails updatedAddress, String token) {
        if (addressId == null) {
            throw new RuntimeException("Cannot update address: addressId is null");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Role", "admin");

        HttpEntity<AddressDetails> request = new HttpEntity<>(updatedAddress, headers);

        restTemplate.exchange(
            AUTH_SERVICE_URL + "/updateById/" + addressId,
            HttpMethod.PUT,
            request,
            AddressDetails.class
        );
    }

    // Helper method to get addressId by email from auth-service with token header
    private Long getAddressIdByEmail(String email, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Role", "ADMIN");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<AddressDetails> response = restTemplate.exchange(
            AUTH_SERVICE_URL + "/getByEmail/" + email,
            HttpMethod.GET,
            entity,
            AddressDetails.class
        );

        AddressDetails address = response.getBody();
        if (address == null || address.getAddressId() == null) {
            throw new RuntimeException("No address found for email: " + email);
        }
        return address.getAddressId();
    }

    // Helper method to enrich PlaneOwner with address from auth-service with token header
    private void enrichWithAddress(PlaneOwner owner, String token) {
        if (owner.getAddressId() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Role", "ADMIN");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<AddressDetails> response = restTemplate.exchange(
                AUTH_SERVICE_URL + "/getById/" + owner.getAddressId(),
                HttpMethod.GET,
                request,
                AddressDetails.class
            );

            AddressDetails address = response.getBody();
            if (address != null) {
                owner.setAddress(address);
            }
        }
    }
}
