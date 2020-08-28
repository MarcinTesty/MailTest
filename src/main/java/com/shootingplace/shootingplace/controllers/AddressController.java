package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.Address;
import com.shootingplace.shootingplace.services.AddressService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/address")
@CrossOrigin
public class AddressController {

    private final AddressService addressService;


    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/{memberUUID}")
    public void addMemberAddress(@PathVariable UUID memberUUID, @RequestBody Address address) {
        addressService.addAddress(memberUUID, address);
    }

    @PutMapping("/{memberUUID}")
    public void updateMemberAddress(@PathVariable UUID memberUUID, @RequestBody Address address) {
        addressService.updateAddress(memberUUID, address);
    }
}