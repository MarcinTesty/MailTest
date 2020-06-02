package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.AddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;


    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }
//
//    @PostMapping("/{memberuuid}")
//    public boolean addAddress(@PathVariable UUID memberuuid, @RequestBody @Valid Address address) {
//        return addressService.addAddress(memberuuid, address);
//    }
//
//    @PutMapping("/{memberuuid}")
//    public boolean updateAddress(@PathVariable UUID memberuuid, @RequestBody Address address) {
//        return addressService.updateAddress(memberuuid, address);
//    }
//

}