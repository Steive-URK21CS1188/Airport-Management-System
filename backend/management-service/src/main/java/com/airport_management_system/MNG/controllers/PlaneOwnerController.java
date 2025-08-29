package com.airport_management_system.MNG.controllers;

import com.airport_management_system.MNG.models.bao.PlaneOwnerBAO;
import com.airport_management_system.MNG.models.pojo.PlaneOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class PlaneOwnerController {

    @Autowired
    private PlaneOwnerBAO planeOwnerBAO;

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new RuntimeException("Invalid Authorization header");
        }
    }

    @PostMapping("/add")
    public PlaneOwner add(@RequestBody PlaneOwner owner,
                          @RequestHeader("Authorization") String authorizationHeader,
                          @RequestHeader("Role") String role) {

        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.addOwner(owner, token, role);
    }

    @GetMapping("/getAll")
    public List<PlaneOwner> getAll(@RequestHeader("Authorization") String authorizationHeader,
                                   @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.getAll(token, role);
    }

    @GetMapping("/getByPlaneNumber/{planeNumber}")
    public PlaneOwner getByPlaneNumber(@PathVariable String planeNumber,
                                       @RequestHeader("Authorization") String authorizationHeader,
                                       @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.getOwnerByPlaneNumber(planeNumber, token, role)
                .orElseThrow(() -> new RuntimeException("Owner not found for plane number: " + planeNumber));
    }

    @GetMapping("/getByAddressEmail/{email}")
    public PlaneOwner getByAddressEmail(@PathVariable String email,
                                        @RequestHeader("Authorization") String authorizationHeader,
                                        @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.fetchByAddressEmail(email, token, role)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @PutMapping("/updateByAddressEmail/{email}")
    public PlaneOwner updateByAddressEmail(@PathVariable String email,
                                           @RequestBody PlaneOwner updated,
                                           @RequestHeader("Authorization") String authorizationHeader,
                                           @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.updateByAddressEmail(email, updated, token, role);
    }

    @DeleteMapping("/deleteByAddressEmail/{email}")
    public void deleteByAddressEmail(@PathVariable String email,
                                     @RequestHeader("Authorization") String authorizationHeader,
                                     @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        planeOwnerBAO.deleteByAddressEmail(email, token, role);
    }

    @GetMapping("/getById/{ownerId}")
    public PlaneOwner getById(@PathVariable Long ownerId,
                              @RequestHeader("Authorization") String authorizationHeader,
                              @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.getById(ownerId, token, role);
    }

    @PutMapping("/updateById/{ownerId}")
    public PlaneOwner updateById(@PathVariable Long ownerId,
                                 @RequestBody PlaneOwner updated,
                                 @RequestHeader("Authorization") String authorizationHeader,
                                 @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.updateById(ownerId, updated, token, role);
    }

    @DeleteMapping("/deleteById/{ownerId}")
    public String deleteById(@PathVariable Long ownerId,
                             @RequestHeader("Authorization") String authorizationHeader,
                             @RequestHeader("Role") String role) {
        String token = extractToken(authorizationHeader);
        return planeOwnerBAO.deleteById(ownerId, token, role);
    }
}
