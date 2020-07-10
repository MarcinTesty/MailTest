package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.ShootingPatent;
import com.shootingplace.shootingplace.services.ShootingPatentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patent")
@CrossOrigin
public class ShootingPatentController {

    private final ShootingPatentService shootingPatentService;

    public ShootingPatentController(ShootingPatentService shootingPatentService) {
        this.shootingPatentService = shootingPatentService;
    }

    @PostMapping("/{memberUUID}")
    public boolean addPatent(@PathVariable UUID memberUUID, @RequestBody ShootingPatent shootingPatent) {
        return shootingPatentService.addPatent(memberUUID, shootingPatent);
    }

    @PutMapping("/{memberUUID}")
    public boolean updatePatent(@PathVariable UUID memberUUID, @RequestBody ShootingPatent shootingPatent) {
        return shootingPatentService.updatePatent(memberUUID, shootingPatent);
    }


}
