package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.ShootingPatent;
import com.shootingplace.shootingplace.services.ShootingPatentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patent")
@CrossOrigin(origins = "https://localhost:8081")
public class ShootingPatentController {

    private final ShootingPatentService shootingPatentService;

    public ShootingPatentController(ShootingPatentService shootingPatentService) {
        this.shootingPatentService = shootingPatentService;
    }

    @PutMapping("/{memberUUID}")
    public ResponseEntity<?> updatePatent(@PathVariable UUID memberUUID, @RequestBody ShootingPatent shootingPatent) {
        if (shootingPatentService.updatePatent(memberUUID, shootingPatent)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
