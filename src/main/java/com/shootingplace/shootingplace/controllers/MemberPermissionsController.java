package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.MemberPermissionsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
@CrossOrigin
public class MemberPermissionsController {

    private final MemberPermissionsService memberPermissionsService;

    public MemberPermissionsController(MemberPermissionsService memberPermissionsService) {
        this.memberPermissionsService = memberPermissionsService;
    }
}
