package com.airport_management_system.AirportManagementSystem;

import com.airport_management_system.MNG.models.customExceptions.*;
import com.airport_management_system.MNG.models.dao.serviceImpl.*;
import com.airport_management_system.MNG.models.dto.AddressDetails;
import com.airport_management_system.MNG.models.dto.PlaneAllocationRequest;
import com.airport_management_system.MNG.models.pojo.*;
import com.airport_management_system.MNG.models.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportManagementSystemApplicationTests {

    // ------------------- PLANE SERVICE -------------------

    @Mock
    private PlaneRepository planeRepository;

    @Mock
    private PlaneOwnerRepository planeOwnerRepository;

    @InjectMocks
    private PlaneServiceImpl planeService;

    private Plane plane;

    @BeforeEach
    void setUpPlane() {
        plane = new Plane();
        plane.setPlaneNumber("ABC123");
        plane.setModel("Boeing 747");
        plane.setCapacity(200);
        plane.setOwner(new PlaneOwner());
    }

    @Test
    void testUpdatePlaneSuccess() {
        Plane updated = new Plane();
        updated.setPlaneNumber("XYZ789");
        updated.setModel("Airbus A320");
        updated.setCapacity(150);

        when(planeRepository.findByPlaneNumber("ABC123")).thenReturn(Optional.of(plane));
        when(planeRepository.save(any())).thenReturn(updated);

        Plane result = planeService.updatePlane("ABC123", updated);

        assertEquals("XYZ789", result.getPlaneNumber());
        assertEquals("Airbus A320", result.getModel());
    }

    @Test
    void testUpdatePlaneNotFound() {
        when(planeRepository.findByPlaneNumber("XXX")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> planeService.updatePlane("XXX", plane));
    }

    @Test
    void testDeleteById() {
        planeService.deleteById(1L);
        verify(planeRepository).deleteById(1L);
    }

    @Test
    void testDeleteByPlaneNumber() {
        planeService.deleteByPlaneNumber("ABC123");
        verify(planeRepository).deleteByPlaneNumber("ABC123");
    }

    // ------------------- PILOT SERVICE -------------------

    @Mock
    private PilotRepository pilotRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PilotServiceImpl pilotService;

    private Pilot pilot;

    @BeforeEach
    void setUpPilot() {
        pilot = new Pilot();
        pilot.setPilotId(10L);
        pilot.setName("John Doe");
        pilot.setAddressId(101L);
    }

    @Test
    void testSavePilotWithAddressFetched() {
        AddressDetails address = new AddressDetails();
        address.setAddressId(101L);
        address.setCity("Hyderabad");

        when(pilotRepository.save(any(Pilot.class))).thenReturn(pilot);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AddressDetails.class)))
                .thenReturn(ResponseEntity.ok(address));

        Pilot saved = pilotService.save(pilot, "token123");

        assertNotNull(saved.getAddress());
        assertEquals("Hyderabad", saved.getAddress().getCity());
    }

    @Test
    void testFindByIdNotFound() {
        when(pilotRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Pilot> result = pilotService.findById(99L, "token123");
        assertTrue(result.isEmpty());
    }

    // ------------------- PLANE ALLOCATION SERVICE -------------------

    @Mock
    private PlaneAllocationRepository allocationRepo;

    @InjectMocks
    private PlaneAllocationServiceImpl allocationService;

    private PlaneAllocationRequest allocationRequest;

    @Captor
    private ArgumentCaptor<PlaneAllocation> allocationCaptor;

    @BeforeEach
    void setUpAllocation() {
        allocationRequest = new PlaneAllocationRequest();
        allocationRequest.setPlaneId(1L);
        allocationRequest.setPilotId(2L);
        allocationRequest.setManagerUserId(3L);
        allocationRequest.setFromDate(LocalDateTime.now());
        allocationRequest.setToDate(LocalDateTime.now().plusHours(2));
    }

    @Test
    void testAllocatePlaneToPilotSuccess() {
        when(allocationRepo.existsOverlappingAllocation(anyLong(), any(), any())).thenReturn(false);
        when(allocationRepo.existsOverlappingAllocationForPlane(anyLong(), any(), any())).thenReturn(false);
        when(allocationRepo.save(any(PlaneAllocation.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = allocationService.allocatePlaneToPilot(allocationRequest);

        assertEquals("Plane successfully allocated to pilot!", result);
        verify(allocationRepo).save(allocationCaptor.capture());
        assertEquals(allocationRequest.getPilotId(), allocationCaptor.getValue().getPilotId());
    }

    @Test
    void testAllocateFailsDueToPilotBusy() {
        when(allocationRepo.existsOverlappingAllocation(eq(allocationRequest.getPilotId()), any(), any())).thenReturn(true);
        assertThrows(PilotUnavailableException.class, () -> allocationService.allocatePlaneToPilot(allocationRequest));
    }

    @Test
    void testAllocateFailsDueToPlaneBusy() {
        when(allocationRepo.existsOverlappingAllocation(anyLong(), any(), any())).thenReturn(false);
        when(allocationRepo.existsOverlappingAllocationForPlane(eq(allocationRequest.getPlaneId()), any(), any())).thenReturn(true);
        assertThrows(PlaneUnavailableException.class, () -> allocationService.allocatePlaneToPilot(allocationRequest));
    }

    @Test
    void testGetAllAllocations() {
        PlaneAllocation allocation = new PlaneAllocation(1L, 2L,
                allocationRequest.getFromDate(), allocationRequest.getToDate(), 3L);
        when(allocationRepo.findAll()).thenReturn(Collections.singletonList(allocation));
        assertEquals(1, allocationService.getAllAllocations().size());
    }

 // ------------------- HANGAR SERVICE -------------------

    @Mock
    private HangarRepository hangarRepository;

    @InjectMocks
    private HangarServiceImpl hangarService;

    private Hangar hangar;

    @BeforeEach
    void setUpHangar() {
        hangar = new Hangar();
        hangar.setHangarId(1L);
        hangar.setHangarName("Main Hangar");
        hangar.setHangarLocation("Hyderabad");
        hangar.setUserId(100L);
        hangar.setCapacity(3);  }

    @Test
    void testAddHangarSuccess() {
        when(hangarRepository.save(hangar)).thenReturn(hangar);
        Hangar result = hangarService.addHangar(hangar);
        assertEquals(hangar, result);
    }

    @Test
    void testAddHangarInvalid() {
        hangar.setHangarName(""); // invalid
        assertThrows(InvalidHangarException.class, () -> hangarService.addHangar(hangar));
    }

    // ------------------- HANGAR ALLOCATION SERVICE -------------------

    @Mock
    private HangarAllocationRepository hangarAllocationRepo;

    @InjectMocks
    private HangarAllocationServiceImpl hangarAllocService;

    private HangarAllocation hangarAllocation;

    @BeforeEach
    void setUpHangarAllocation() {
        hangarAllocation = new HangarAllocation();
        hangarAllocation.setPlaneId(1L);
        hangarAllocation.setHangarId(2L);
        hangarAllocation.setFromDate(Timestamp.valueOf(LocalDateTime.now()));
        hangarAllocation.setToDate(Timestamp.valueOf(LocalDateTime.now().plusHours(2)));
    }

    @Test
    void testAllocateHangar() {
        when(hangarAllocationRepo.save(any(HangarAllocation.class))).thenReturn(hangarAllocation);
        HangarAllocation saved = hangarAllocService.allocateHangar(hangarAllocation);
        assertNotNull(saved);
        assertEquals(1L, saved.getPlaneId());
    }

    @Test
    void testGetAllHangarAllocations() {
        when(hangarAllocationRepo.findAll()).thenReturn(Collections.singletonList(hangarAllocation));
        var result = hangarAllocService.getAllAllocations();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPlaneId());
    }
}
