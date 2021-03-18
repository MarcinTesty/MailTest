package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@CrossOrigin
public class SettingsController {

    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createSuperUser")
    public ResponseEntity<?> createSuperUser(@RequestParam String name, @RequestParam String pinCode) {
        return ResponseEntity.ok(userService.createSuperUser(name, pinCode));
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestParam String name, @RequestParam String pinCode, @RequestParam String superPinCode) {
        return ResponseEntity.ok(userService.createUser(name, pinCode, superPinCode));
    }
}
