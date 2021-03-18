package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.WeaponPermission;
import com.shootingplace.shootingplace.services.WeaponPermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weapon")
@CrossOrigin
public class WeaponController {

    private final WeaponPermissionService weaponPermissionService;

    public WeaponController(WeaponPermissionService weaponPermissionService) {
        this.weaponPermissionService = weaponPermissionService;
    }

    @PutMapping("/weapon/{memberUUID}")
    public ResponseEntity<?> changeWeaponPermission(@PathVariable String memberUUID, @RequestBody WeaponPermission weaponPermission) {
        if (weaponPermissionService.updateWeaponPermission(memberUUID, weaponPermission)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/weapon/{memberUUID}")
    public ResponseEntity<?> removeWeaponPermission(@PathVariable String memberUUID, @RequestParam boolean admission, @RequestParam boolean permission) {
        if (weaponPermissionService.removeWeaponPermission(memberUUID, admission, permission)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
