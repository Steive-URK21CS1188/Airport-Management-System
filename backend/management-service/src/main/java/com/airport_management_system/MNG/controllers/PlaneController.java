package com.airport_management_system.MNG.controllers;

import com.airport_management_system.MNG.models.bao.PlaneBAO;
import com.airport_management_system.MNG.models.pojo.Plane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlaneController {

    @Autowired
    private PlaneBAO planeBAO;
    @PostMapping("/add")
    public Plane addPlane(@RequestBody Plane plane,
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

        return planeBAO.addPlane(plane, token, role);
    }


    @GetMapping("/getAll")
    public List<Plane> getAllPlanes(@RequestParam String token, @RequestHeader("Role") String role) {
        return planeBAO.getAllPlanes(token, role);
    }

    @GetMapping("/getByNumber/{planeNumber}")
    public Plane getByNumber(@PathVariable String planeNumber,
    						@RequestParam String token,
                             @RequestHeader("Role") String role) {
        return planeBAO.getPlane(planeNumber,token, role);
    }

    
    
    @PutMapping("/edit/{planeNumber}")
    public Plane editPlane(@PathVariable String planeNumber,
                           @RequestBody Plane updatedPlane,
                           @RequestHeader("Role") String role) {
        return planeBAO.updatePlane(planeNumber, updatedPlane, role);
    }

    @GetMapping("/getById/{planeId}")
    public Plane getById(@PathVariable Long planeId,
    		@RequestParam String token,
                         @RequestHeader("Role") String role) {
        return planeBAO.getById(planeId,token, role);
    }

    @PutMapping("/updateById/{planeId}")
    public Plane updateById(@PathVariable Long planeId,
                            @RequestBody Plane updated,
                            @RequestParam String token,
                            @RequestHeader("Role") String role) {
    	
        return planeBAO.updateById(planeId,updated,token,role);
    }

    @DeleteMapping("/deleteById/{planeId}")
    public void deleteById(@PathVariable Long planeId,
                             @RequestHeader("Role") String role) {
        planeBAO.deleteById(planeId, role);
    }

    
    @DeleteMapping("/delete/{planeNumber}")
    public void deleteByNumber(@PathVariable String planeNumber,
                               @RequestHeader("Role") String role) {
        planeBAO.deletePlane(planeNumber, role);
    }
}
