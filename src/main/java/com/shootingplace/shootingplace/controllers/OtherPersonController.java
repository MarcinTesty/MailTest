package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.OtherPerson;
import com.shootingplace.shootingplace.services.OtherPersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> addPerson(@RequestBody OtherPerson person, @RequestParam String club) {
        if (otherPersonService.addPerson(club, person)) {
            return ResponseEntity.status(201).build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/")
    public ResponseEntity<List<String>> getAllOthers(){
        return ResponseEntity.ok().body(otherPersonService.getAllOthers());
    }


}
