package com.shootingplace.shootingplace.controllers;


import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Member;
import com.shootingplace.shootingplace.domain.models.MemberDTO;
import com.shootingplace.shootingplace.services.ChangeHistoryService;
import com.shootingplace.shootingplace.services.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/member")
@CrossOrigin
public class MemberController {

    private final MemberService memberService;
    private final ChangeHistoryService changeHistoryService;


    public MemberController(MemberService memberService, ChangeHistoryService changeHistoryService) {
        this.memberService = memberService;
        this.changeHistoryService = changeHistoryService;
    }

    @GetMapping("/{number}")
    public ResponseEntity<MemberEntity> getMember(@PathVariable int number) {
        return ResponseEntity.ok(memberService.getMember(number));
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<MemberEntity> getMemberByUUID(@PathVariable String uuid) {
        return ResponseEntity.ok(memberService.getMemberByUUID(uuid));
    }

    @GetMapping("/activeList")
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
        return memberService.getMembersNameAndLegitimationNumber(active, adult, erase);
    }

    @GetMapping("/getAllNames")
    public List<String> getAllNames() {
        return memberService.getAllNames();
    }

    @GetMapping("/getAllMemberDTO")
    public ResponseEntity<List<MemberDTO>> getAllMemberDTO() {
        return ResponseEntity.ok(memberService.getAllMemberDTO());

    }

    @GetMapping("/getAllMemberDTOWithArgs")
    public ResponseEntity<List<MemberDTO>> getAllMemberDTO(@RequestParam boolean adult, @RequestParam boolean active, @RequestParam boolean erase) {
        return ResponseEntity.ok(memberService.getAllMemberDTO(adult, active, erase));

    }

    @GetMapping("/membersQuantity")
    public List<Long> getMembersQuantity() {
        return memberService.getMembersQuantity();
    }

    @GetMapping("/getAllActiveMembersNames")
    public List<String> getAllActiveMembersNames() {
        return memberService.getAllActiveMembersNames();
    }

    @GetMapping("/getArbiters")
    public List<String> getArbiters() {
        return memberService.getArbiters();
    }

    @GetMapping("/membersWithPermissions")
    public List<Member> getMembersWithPermissions() {
        return memberService.getMembersWithPermissions();
    }

    @GetMapping("/membersEmails")
    public ResponseEntity<?> getMembersEmails(@RequestParam Boolean condition) {
        return ResponseEntity.ok(memberService.getMembersEmails(condition));
    }

    @GetMapping("/membersToEraseEmails")
    public ResponseEntity<?> getMembersToEraseEmails() {
        return ResponseEntity.ok(memberService.getMembersToEraseEmails());
    }

    @GetMapping("/membersToErasePhoneNumbers")
    public ResponseEntity<?> getMembersToErasePhoneNumbers() {
        return ResponseEntity.ok(memberService.getMembersToErasePhoneNumbers());
    }

    @GetMapping("/membersToPoliceEmails")
    public ResponseEntity<?> getMembersToPoliceEmails() {
        return ResponseEntity.ok(memberService.getMembersToPoliceEmails());
    }

    @GetMapping("/membersToPolicePhoneNumbers")
    public ResponseEntity<?> getMembersToPolicePhoneNumbers() {
        return ResponseEntity.ok(memberService.getMembersToPolicePhoneNumbers());
    }


    @GetMapping("/phoneNumbers")
    public ResponseEntity<?> getMembersPhoneNumbers(@RequestParam Boolean condition) {
        return ResponseEntity.ok(memberService.getMembersPhoneNumbers(condition));
    }

    @GetMapping("/erasedType")
    public ResponseEntity<?> getErasedType() {
        return ResponseEntity.ok(memberService.getErasedType());
    }

    @PostMapping("/")
    public ResponseEntity<?> addMember(@RequestBody @Valid Member member, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {
            ResponseEntity<?> result;
            if (member.getPesel().isEmpty() || member.getPhoneNumber().isEmpty() || member.getFirstName().isEmpty() || member.getSecondName().isEmpty() || member.getIDCard().isEmpty()) {
                result = ResponseEntity.status(406).body("\"Uwaga! Nie podano wszystkich lub żadnej informacji\"");
            } else {
                try {
                    result = memberService.addNewMember(member, pinCode);
                } catch (IllegalArgumentException e) {
                    result = ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
            return result;
        } else {
            return ResponseEntity.status(403).body("Brak dostępu");
        }
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateMember(@PathVariable String uuid, @RequestBody @Valid Member member) {
        return memberService.updateMember(uuid, member);
    }

    @PutMapping("/date/{uuid}")
    public ResponseEntity<?> updateJoinDate(@PathVariable String uuid, @RequestParam String date) {
        return memberService.updateJoinDate(uuid, date);
    }

    @PatchMapping("/adult/{uuid}")
    public ResponseEntity<?> changeAdult(@PathVariable String uuid, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {

            return memberService.changeAdult(uuid, pinCode);
        } else {
            return ResponseEntity.status(403).body("Brak dostępu");
        }
    }

    @PatchMapping("/pzss/{uuid}")
    public ResponseEntity<?> changePzss(@PathVariable String uuid) {
        if (memberService.changePzss(uuid)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<?> activateOrDeactivateMember(@PathVariable String uuid, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {
            return memberService.activateOrDeactivateMember(uuid, pinCode);
        } else {
            return ResponseEntity.status(403).body("Brak dostępu");
        }
    }

    @PatchMapping("/erase/{uuid}")
    public ResponseEntity<?> eraseMember(@PathVariable String uuid, @RequestParam String additionalDescription, @RequestParam String erasedDate, @RequestParam String erasedType, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {
            if (additionalDescription.trim().isEmpty() || additionalDescription.trim().isEmpty() || additionalDescription.trim().equals("null")) {
                additionalDescription = null;
            }
            if (erasedDate.trim().isEmpty() || erasedDate.trim().isEmpty() || erasedDate.trim().equals("null")) {
                erasedDate = String.valueOf(LocalDate.now());
            }
            LocalDate parsedDate = LocalDate.parse(erasedDate);
            return memberService.eraseMember(uuid, erasedType, parsedDate, additionalDescription, pinCode);
        } else {
            return ResponseEntity.status(403).body("Brak dostępu");
        }
    }

}
