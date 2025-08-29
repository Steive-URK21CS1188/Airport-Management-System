package com.airport_management_system.MNG.models.bao;

import com.airport_management_system.MNG.models.customExceptions.ResourceNotFoundException;
import com.airport_management_system.MNG.models.dao.services.PilotService;
import com.airport_management_system.MNG.models.dto.AddressDetails;
import com.airport_management_system.MNG.models.dto.PilotDTO;
import com.airport_management_system.MNG.models.pojo.Pilot;
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
public class PilotBAO {

    @Autowired
    private PilotService pilotDAO;

    @Autowired
    private RestTemplate restTemplate;

    private static final String ADDRESS_SERVICE_URL = "http://AuthenticationApp/api/address";

    public Pilot addPilotFromDTO(PilotDTO pilotDTO, String token, String role) {
        Pilot pilot = new Pilot();
        pilot.setName(pilotDTO.getName());
        pilot.setLicenseNo(pilotDTO.getLicenseNo());
        pilot.setUserId(pilotDTO.getUserId());

        AddressDetails address = pilotDTO.getAddress();
        if (address != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Role", "ADMIN");

            HttpEntity<AddressDetails> requestEntity = new HttpEntity<>(address, headers);

            if (address.getAddressId() != null) {
                // ✅ Corrected endpoint
                String updateUrl = ADDRESS_SERVICE_URL + "/updateById/" + address.getAddressId();
                restTemplate.exchange(updateUrl, HttpMethod.PUT, requestEntity, AddressDetails.class);
                pilot.setAddressId(address.getAddressId());
            } else {
                ResponseEntity<AddressDetails> response = restTemplate.exchange(
                        ADDRESS_SERVICE_URL,
                        HttpMethod.POST,
                        requestEntity,
                        AddressDetails.class);
                pilot.setAddressId(response.getBody().getAddressId());
            }
        } else {
            throw new IllegalArgumentException("Address details must be provided");
        }

        return pilotDAO.save(pilot, token);
    }

    public List<Pilot> getAllPilots(String token) {
        return pilotDAO.findAll(token);
    }

    public Optional<Pilot> getPilotById(Long id, String token) {
        return pilotDAO.findById(id, token);
    }

    public void deletePilotById(Long id) {
        pilotDAO.deleteById(id);
    }

    public Pilot updatePilotFromDTO(Long id, PilotDTO pilotDTO, String token, String role) {
        Pilot pilot = pilotDAO.findById(id, token)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found with id: " + id));

        pilot.setName(pilotDTO.getName());
        pilot.setLicenseNo(pilotDTO.getLicenseNo());
        pilot.setUserId(pilotDTO.getUserId());

        AddressDetails address = pilotDTO.getAddress();
        if (address != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Role", role);

            HttpEntity<AddressDetails> requestEntity = new HttpEntity<>(address, headers);

            if (address.getAddressId() != null) {
                // ✅ Corrected endpoint
                String updateUrl = ADDRESS_SERVICE_URL + "/updateById/" + address.getAddressId();
                restTemplate.exchange(updateUrl, HttpMethod.PUT, requestEntity, AddressDetails.class);
                pilot.setAddressId(address.getAddressId());
            } else {
                ResponseEntity<AddressDetails> response = restTemplate.exchange(
                        ADDRESS_SERVICE_URL,
                        HttpMethod.POST,
                        requestEntity,
                        AddressDetails.class);
                pilot.setAddressId(response.getBody().getAddressId());
            }
        }

        return pilotDAO.save(pilot, token);
    }

    public Pilot updatePilot(Long id, Pilot pilotDetails, String token) {
        Pilot pilot = pilotDAO.findById(id, token)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found with id: " + id));

        pilot.setName(pilotDetails.getName());
        pilot.setLicenseNo(pilotDetails.getLicenseNo());
        pilot.setUserId(pilotDetails.getUserId());

        return pilotDAO.save(pilot, token);
    }
}
