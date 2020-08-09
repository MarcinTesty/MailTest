package com.shootingplace.shootingplace.controllers;


import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Member;
import com.shootingplace.shootingplace.domain.models.WeaponPermission;
import com.shootingplace.shootingplace.services.MemberService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/member")
@CrossOrigin
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{uuid}")
    public Optional<MemberEntity> getSingleMember(@PathVariable UUID uuid) {
        return memberService.getSingleMember(uuid);
    }

    @GetMapping("/list")
    public Map<UUID, Member> getMembers() {
        return memberService.getMembers();
    }

    @GetMapping("/active")
    public Map<UUID, Member> getActiveMembers() {
        return memberService.getActiveMembers();
    }

    @GetMapping("/activelist")
    public List<MemberEntity> getActiveMembersList(@RequestParam Boolean active, @RequestParam Boolean adult, @RequestParam Boolean erase) {
        return memberService.getActiveMembersList(active, adult, erase);
    }

    @GetMapping("/nonactivelist")
    public List<MemberEntity> getNonActiveList(@RequestParam Boolean active, @RequestParam Boolean erased) {
        return memberService.getNonActiveMembers(active, erased);
    }

    @GetMapping("/erased")
    public List<MemberEntity> getErasedMembers(@RequestParam Boolean erased) {
        return memberService.getErasedMembers(erased);
    }

    @GetMapping("/license")
    public Map<String, String> getMemberWithLicenseNumberEqualsNotNull() {
        return memberService.getMembersNamesWithLicenseNumberEqualsNotNull();
    }

    @GetMapping("/licensebefore")
    public Map<String, String> getMembersNamesWithLicenseNumberEqualsNotNullAndValidThruIsBefore() {
        return memberService.getMembersNamesWithLicenseNumberEqualsNotNullAndValidThruIsBefore();
    }

    @GetMapping("/licensenone")
    public List<String> getMembersNamesWithoutLicense() {
        return memberService.getMembersNamesWithoutLicense();
    }

    @GetMapping("/contribution")
    public Map<String, String> getMembersAndTheirsContribution() {
        return memberService.getMembersAndTheirsContribution();
    }

    @GetMapping("/contributionafter")
    public Map<String, String> getMembersAndTheirsContributionIsValid() {
        return memberService.getMembersAndTheirsContributionIsValid();
    }

    @GetMapping("/contributionbefore")
    public Map<String, String> getMembersAndTheirsContributionIsNotValid() {
        return memberService.getMembersAndTheirsContributionIsNotValid();
    }

    @GetMapping("/licensewithoutcontribution")
    public Map<String, String> getMembersWhoHaveValidLicenseAndNotValidContribution() {
        return memberService.getMembersWhoHaveValidLicenseAndNotValidContribution();

    }

    @PostMapping("/")
    public UUID addMember(@RequestBody @Valid Member member) {
        try {
            return memberService.addNewMember(member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PutMapping("/{uuid}")
    public boolean updateMember(@PathVariable UUID uuid, @RequestBody Member member) {
        return memberService.updateMember(uuid, member);
    }

    @DeleteMapping("/{uuid}")
    public boolean deleteMember(@PathVariable UUID uuid) {

        return memberService.deleteMember(uuid);
    }

    @PatchMapping("/{uuid}")
    public boolean activateOrDeactivateMember(@PathVariable UUID uuid) {
        return memberService.activateOrDeactivateMember(uuid);
    }

    @PutMapping("/weapon/{uuid}")
    public boolean changeWeaponPermission(@PathVariable UUID uuid, @RequestBody WeaponPermission weaponPermission) {
        return memberService.changeWeaponPermission(uuid, weaponPermission);
    }

    @PatchMapping("/adult/{uuid}")
    public boolean changeAdult(@PathVariable UUID uuid) {
        return memberService.changeAdult(uuid);
    }

    @PatchMapping("/erase/{uuid}")
    public boolean eraseMember(@PathVariable UUID uuid) {
        return memberService.eraseMember(uuid);
    }


}
