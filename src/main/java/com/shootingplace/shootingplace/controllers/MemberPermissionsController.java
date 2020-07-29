package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.MemberPermissions;
import com.shootingplace.shootingplace.services.MemberPermissionsService;
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
    public Boolean updateMemberPermissions(@PathVariable UUID memberUUID,
                                           @RequestBody MemberPermissions memberPermissions,@RequestParam String ordinal) {
        return memberPermissionsService.updateMemberPermissions(memberUUID, memberPermissions,ordinal);
    }
}
