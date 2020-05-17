package com.shootingplace.shootingplace.Services;

import com.shootingplace.shootingplace.Repositories.MemberRepository;
import com.shootingplace.shootingplace.domain.Entities.MemberEntity;
import com.shootingplace.shootingplace.domain.Models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;


    public Map<UUID, Member> getMembers() {
        Map<UUID, Member> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> map.put(e.getUuid(), map(e)));
        return map;
    }


    public UUID addMember(Member member) {
        return memberRepository.saveAndFlush(map(member)).getUuid();
    }


    public boolean deleteMember(UUID uuid) {

        if (memberRepository.existsById(uuid)) {
            memberRepository.deleteById(uuid);
            return true;
        } else
            return false;
    }


    //--------------------------------------------------------------------------
    public boolean updateMember(UUID uuid, Member member) {
        try {
            MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);

            if (member.getFirstName() != null) {
                memberEntity.setFirstName(member.getFirstName());
            }
            if (member.getSecondName() != null) {
                memberEntity.setSecondName(member.getSecondName());
            }
            if (member.getLicenseNumber() != null) {
                memberEntity.setLicenseNumber(member.getLicenseNumber());
            }
            if (member.getEmail() != null) {
                memberEntity.setEmail(member.getEmail());
            }
            if (member.getPesel() != null) {
                memberEntity.setPesel(member.getPesel());
            }
            if (member.getAddress() != null) {
                memberEntity.setAddress(member.getAddress());
            }
            memberRepository.saveAndFlush(memberEntity);
            goodMessage();
            return true;
        } catch (
                EntityNotFoundException ex) {
            badMessage();
            return false;
        }
    }

    private void goodMessage() {
        System.out.println("Zaktualizowano pomyślnie");
    }

    private void badMessage() {
        System.out.println("Nie udało się zaktualizować bo Klubowicz nieistnieje");
    }

    //--------------------------------------------------------------------------

    // Mapping
    private Member map(MemberEntity e) {
        return Member.builder()
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .licenseNumber(e.getLicenseNumber())
                .email(e.getEmail())
                .pesel(e.getPesel())
                .address(e.getAddress())
                .build();
    }

    private MemberEntity map(Member e) {
        return MemberEntity.builder()
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .licenseNumber(e.getLicenseNumber())
                .email(e.getEmail())
                .pesel(e.getPesel())
                .address(e.getAddress())
                .build();
    }
}
