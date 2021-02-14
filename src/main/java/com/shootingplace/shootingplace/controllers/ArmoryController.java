package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.ArmoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/armory")
@CrossOrigin
public class ArmoryController {

    private final ArmoryService armoryService;

    public ArmoryController(ArmoryService armoryService) {
        this.armoryService = armoryService;
    }

    @GetMapping("/quantitySum")
    public ResponseEntity<?> getSumFromAllAmmoList(){
//        armoryService.update2();
        return ResponseEntity.ok(armoryService.getSumFromAllAmmoList(LocalDate.of(2021,1,1),LocalDate.now()));
    }

}
