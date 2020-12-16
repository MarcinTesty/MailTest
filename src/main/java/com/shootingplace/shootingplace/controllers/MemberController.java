package com.shootingplace.shootingplace.controllers;


import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Member;
import com.shootingplace.shootingplace.domain.models.WeaponPermission;
import com.shootingplace.shootingplace.services.MemberService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/member")
@CrossOrigin
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{memberUUID}")
    public ResponseEntity<MemberEntity> getMember(@PathVariable UUID memberUUID) {
        return ResponseEntity.ok(memberService.getMember(memberUUID));
    }

    @GetMapping("/activelist")
    public ResponseEntity<List<MemberEntity>> getActiveMembersList(@RequestParam Boolean active, @RequestParam Boolean adult, @RequestParam Boolean erase) {
        return ResponseEntity.ok(memberService.getMembersList(active, adult, erase));
    }

    @GetMapping("/erased")
    public ResponseEntity<List<MemberEntity>> getErasedMembers() {
        return ResponseEntity.ok(memberService.getErasedMembers());
    }

    @GetMapping("/license")
    public ResponseEntity<List<String>> getMemberWithLicense(@RequestParam Boolean license) {
        return ResponseEntity.ok(memberService.getMembersWithLicense(license));
    }

    @GetMapping("/getMembersNames")
    public List<String> getMembersNames(@RequestParam Boolean active, @RequestParam Boolean adult, @RequestParam Boolean erase) {
        return memberService.getMembersNameAndUUID(active, adult, erase);
    }

    @GetMapping("/memberswithpermissions")
    public List<Member> getMembersWithPermissions() {
        return memberService.getMembersWithPermissions();
    }

    @GetMapping("/getArbiters")
    public List<String> getArbiters() {
        return memberService.getArbiters();
    }

    @GetMapping("/membersEmails")
    public String getMembersEmails(@RequestParam Boolean condition) {
        return memberService.getAdultMembersEmails(condition);
    }

    @Transactional
    @PostMapping("/")
    public ResponseEntity<?> addMember(@RequestBody @Valid Member member) {
        ResponseEntity<?> result;
        try {
            result = memberService.addNewMember(member);
        } catch (IOException | DocumentException | IllegalArgumentException e) {
            result = ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return result;
    }

    @PutMapping("/{uuid}")
    ResponseEntity<?> updateMember(@PathVariable UUID uuid, @RequestBody @Valid Member member) {
        return memberService.updateMember(uuid, member);
    }

    @Transactional
    @PutMapping("/date/{uuid}")
    public ResponseEntity<?> updateJoinDate(@PathVariable UUID uuid, @RequestParam String date) {
        return memberService.updateJoinDate(uuid, date);
    }

    @PutMapping("/weapon/{uuid}")
    public boolean changeWeaponPermission(@PathVariable UUID uuid, @RequestBody WeaponPermission weaponPermission) {
        return memberService.changeWeaponPermission(uuid, weaponPermission);
    }

    @Transactional
    @PatchMapping("/adult/{uuid}")
    public ResponseEntity<?> changeAdult(@PathVariable UUID uuid) {
        return memberService.changeAdult(uuid);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<?> activateOrDeactivateMember(@PathVariable UUID uuid) {
        return memberService.activateOrDeactivateMember(uuid);
    }

    @PatchMapping("/erase/{uuid}")
    ResponseEntity<?> eraseMember(@PathVariable UUID uuid) {
        return memberService.eraseMember(uuid);
    }


}
