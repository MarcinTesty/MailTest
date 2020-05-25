package com.shootingplace.shootingplace.controllers;


import com.shootingplace.shootingplace.services.MemberService;
import com.shootingplace.shootingplace.domain.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/member")
public class MemberController {


    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public Map<UUID, Member> getMembers(){return memberService.getMembers();}

    @PostMapping("/")
    public UUID addMember(@RequestBody @Valid Member member){
        return memberService.addMember(member);
    }

    @PutMapping("/{uuid}")
    public boolean updateMember(@PathVariable UUID uuid, @RequestBody Member member){
        return memberService.updateMember(uuid, member);
    }
    @DeleteMapping("/{uuid}")
    public boolean deleteMember(@PathVariable UUID uuid){
        return memberService.deleteMember(uuid);
    }


}
