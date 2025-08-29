package com.airport_management_system.AMS.controllers;

import com.airport_management_system.AMS.models.bao.AddressBAO;
import com.airport_management_system.AMS.models.pojo.AddressDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressBAO addressBAO;

    @PostMapping
    public AddressDetails createAddress(@RequestBody AddressDetails addressDetails,
                                        @RequestHeader("Role") String role) {
        return addressBAO.createAddress(addressDetails, role);
    }

    @GetMapping("/getByEmail/{email}")
    public AddressDetails getByEmail(@PathVariable String email,
                              @RequestHeader("Role") String role) {
        return addressBAO.getByEmail(email, role);
    }

    @GetMapping("/getAll")
    public List<AddressDetails> getAll(@RequestHeader("Role") String role) {
        return addressBAO.getAll(role);
    }
    
    @PutMapping("/edit/{email}")
    public AddressDetails editAddress(@PathVariable String email,
                                      @RequestBody AddressDetails updatedAddress,
                                      @RequestHeader("Role") String role) {
        return addressBAO.updateAddressByEmail(email, updatedAddress, role);
    }



    @PutMapping("/updateById/{addressId}")
    public AddressDetails updateById(@PathVariable Long addressId,
                                     @RequestBody AddressDetails updatedAddress,
                                     @RequestHeader("Role") String role) {
        return addressBAO.updateAddressById(addressId, updatedAddress, role);
    }

    @DeleteMapping("/deleteById/{addressId}")
    public void deleteById(@PathVariable Long addressId,
                           @RequestHeader("Role") String role) {
        addressBAO.deleteAddressById(addressId, role);
    }

    @GetMapping("/getById/{addressId}")
    public AddressDetails getById(@PathVariable Long addressId,
                                  @RequestHeader("Role") String role) {
        return addressBAO.getById(addressId, role);
    }
}
