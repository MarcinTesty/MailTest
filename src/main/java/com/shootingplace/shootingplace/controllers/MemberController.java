package com.shootingplace.shootingplace.controllers;


import com.shootingplace.shootingplace.services.MemberService;
import com.shootingplace.shootingplace.domain.models.Member;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping("/")
    public void addMember(@RequestBody @Valid Member member) {
        memberService.addMember(member);
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
