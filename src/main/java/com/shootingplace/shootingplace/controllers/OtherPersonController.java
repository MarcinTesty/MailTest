package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import com.shootingplace.shootingplace.domain.models.MemberPermissions;
import com.shootingplace.shootingplace.domain.models.OtherPerson;
import com.shootingplace.shootingplace.services.OtherPersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/other")
@CrossOrigin
public class OtherPersonController {
    private final OtherPersonService otherPersonService;

    public OtherPersonController(OtherPersonService otherPersonService) {
        this.otherPersonService = otherPersonService;
    }

    @PostMapping("")
    public ResponseEntity<?> addPerson(@RequestBody OtherPerson person, @RequestParam String club,
                                       @Nullable @RequestParam String arbiterClass,
                                       @Nullable @RequestParam String arbiterNumber,
                                       @Nullable @RequestParam LocalDate arbiterPermissionValidThru) {

        MemberPermissions memberPermissions = MemberPermissions.builder()
                .arbiterNumber(arbiterNumber)
                .arbiterClass(arbiterClass)
                .arbiterPermissionValidThru(arbiterPermissionValidThru)
                .shootingLeaderNumber(null)
                .instructorNumber(null)
                .build();

        if (otherPersonService.addPerson(club, person, memberPermissions)) {
            return ResponseEntity.status(201).build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/")
    public ResponseEntity<List<String>> getAllOthers() {
        return ResponseEntity.ok().body(otherPersonService.getAllOthers());
    }

    @GetMapping("/arbiters")
    public ResponseEntity<List<String>> getAllOthersArbiters(){
        return ResponseEntity.ok().body(otherPersonService.getAllOthersArbiters());
    }
    @GetMapping("/all")
    public ResponseEntity<List<OtherPersonEntity>> getAll(){
        return ResponseEntity.ok().body(otherPersonService.getAll());
    }

}
