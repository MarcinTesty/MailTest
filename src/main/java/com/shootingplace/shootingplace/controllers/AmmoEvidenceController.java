package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Member;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ammo")
@CrossOrigin
public class AmmoEvidenceController {
    private final AmmoEvidenceRepository ammoEvidenceRepository;

    public AmmoEvidenceController(AmmoEvidenceRepository ammoEvidenceRepository) {
        this.ammoEvidenceRepository = ammoEvidenceRepository;
    }

    @GetMapping("/")
    public List<Member> getAmmoMap(){
        return null;
    }


}
