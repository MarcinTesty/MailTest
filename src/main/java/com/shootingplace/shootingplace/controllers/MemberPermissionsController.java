package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.MemberPermissions;
import com.shootingplace.shootingplace.services.MemberPermissionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@CrossOrigin
public class MemberPermissionsController {

    private final MemberPermissionsService memberPermissionsService;

    public MemberPermissionsController(MemberPermissionsService memberPermissionsService) {
        this.memberPermissionsService = memberPermissionsService;
    }

    @PutMapping("/{memberUUID}")
    public ResponseEntity<?> updateMemberPermissions(@PathVariable UUID memberUUID,
                                                     @RequestBody MemberPermissions memberPermissions, @RequestParam String ordinal) {
        if (memberPermissionsService.updateMemberPermissions(memberUUID, memberPermissions, ordinal)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
