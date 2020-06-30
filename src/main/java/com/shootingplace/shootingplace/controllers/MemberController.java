package com.shootingplace.shootingplace.controllers;


import com.shootingplace.shootingplace.domain.models.Member;
import com.shootingplace.shootingplace.services.MemberService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/list")
    public Map<UUID, Member> getMembers() {
        return memberService.getMembers();
    }

    @GetMapping("/active")
    public Map<UUID, Member> getActiveMembers() {
        return memberService.getActiveMembers();
    }

    @GetMapping("/nonactive")
    public Map<UUID, Member> getNonActiveMembers() {
        return memberService.getNonActiveMembers();
    }

    @GetMapping("/activelist")
    public List<String> getActiveMembersList(@RequestParam Boolean a) {
        return memberService.getActiveMembersList(a);
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

    @GetMapping("licensewithoutcontribution")
    public Map<String, String> getMembersWhoHaveValidLicenseAndNotValidContribution() {
        return memberService.getMembersWhoHaveValidLicenseAndNotValidContribution();

    }

    @GetMapping("weaponpermissionwithoutlicense")
    public Map<String, String> getMemberWithWeaponPermissionIsTrueAndWithoutValidLicense() {
        return memberService.getMemberWithWeaponPermissionIsTrueAndWithoutValidLicense();
    }


    @PostMapping("/")
    public UUID addMember(@RequestBody @Valid Member member) {
        return memberService.addMember(member);
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
    public boolean ActivateOrDeactivateMember(@PathVariable UUID uuid) {
        return memberService.activateOrDeactivateMember(uuid);
    }


}
