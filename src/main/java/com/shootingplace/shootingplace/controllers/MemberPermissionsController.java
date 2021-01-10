package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import com.shootingplace.shootingplace.domain.models.MemberPermissions;
import com.shootingplace.shootingplace.services.MemberPermissionsService;
import com.shootingplace.shootingplace.services.OtherPersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@CrossOrigin(origins = "https://localhost:8081")
public class MemberPermissionsController {

    private final MemberPermissionsService memberPermissionsService;
    private final OtherPersonService otherPersonService;


    public MemberPermissionsController(MemberPermissionsService memberPermissionsService, OtherPersonService otherPersonService) {
        this.memberPermissionsService = memberPermissionsService;
        this.otherPersonService = otherPersonService;
    }

    @GetMapping("/othersWithPermissions")
    public List<OtherPersonEntity> getOthersWithPermissions(){
        return otherPersonService.getOthersWithPermissions();
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
