package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;


    //--------------------------------------------------------------------------
    public Map<UUID, Member> getMembers() {
        Map<UUID, Member> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> map.put(e.getUuid(), map(e)));
        System.out.println("Wyświetlono listę członków klubu");
        System.out.println("Ilość klubowiczów : " + memberRepository.findAll().size());
        return map;
    }

    //--------------------------------------------------------------------------
    public void addMember(Member member) {
        if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
            System.out.println("Ktoś już na taki numer PESEL");
        } else if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            System.out.println("Ktoś już ma taki adres e-mail");
        } else if (memberRepository.findByLicenseNumber(member.getLicenseNumber()).isPresent()) {
            System.out.println("Ktoś już ma taki numer licencji");
        } else if (memberRepository.findByShootingPatentNumber(member.getShootingPatentNumber
                ()).isPresent()) {
            System.out.println("Ktoś już ma taki numer patentu");
        } else if (memberRepository.findByLegitimationNumber(member.getLegitimationNumber()).isPresent()) {
            System.out.println("Ktoś już ma taki numer legitymacji");
        } else {
            if (member.getJoinDate() == null) {
                System.out.println("ustawiono domyślną datę zapisu");
                member.setJoinDate(LocalDate.now());
            }
            if (member.getLegitimationNumber() == null) {
                System.out.println("ustawiono domyślny numer legitymacji");
                member.setLegitimationNumber(memberRepository.findAll().size() + 1);
            }
            if (member.getLicenseNumber() == null) {
                System.out.println("Nie ma numeru licencji");
                member.setLicenseNumber(member.getFirstName() + " " + member.getSecondName() + " nie posiada licencji");
            }
            if (member.getShootingPatentNumber() == null) {
                System.out.println("Nie ma numeru patentu");
                member.setShootingPatentNumber(member.getFirstName() + " " + member.getSecondName() + " nie posiada patentu");
            }
            if (member.getAddress() == null) {
                System.out.println("Adres nie został wskazany");
                member.setAddress("nie wskazano adresu");
            }
            System.out.println("Dodano nowego członka Klubu");
            memberRepository.saveAndFlush(map(member));
        }
    }

    //--------------------------------------------------------------------------
    public boolean deleteMember(UUID uuid) {

        if (memberRepository.existsById(uuid)) {
            memberRepository.deleteById(uuid);
            System.out.println("Usunięto członka klubu");
            return true;
        } else
            System.out.println("Taki klubowicz nie istnieje więc nie może być usunięty");
        return false;
    }


    //--------------------------------------------------------------------------
    public boolean updateMember(UUID uuid, Member member) {
        try {
            MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);

            if (member.getFirstName() != null) {
                memberEntity.setFirstName(member.getFirstName());
                System.out.println(goodMessage() + "Imię");
            }
            if (member.getSecondName() != null) {
                memberEntity.setSecondName(member.getSecondName());
                System.out.println(goodMessage() + "Nazwisko");

            }
            if (member.getJoinDate() != null) {
                memberEntity.setJoinDate(member.getJoinDate());
                System.out.println(goodMessage() + "Data przystąpienia do klubu");
            }
            if (member.getLegitimationNumber() != null) {
                if (memberRepository.findByLegitimationNumber(member.getLegitimationNumber()).isPresent()) {
                    System.out.println("Już ktoś ma taki numer legitymacji");
                    return false;
                } else {
                    memberEntity.setLegitimationNumber(member.getLegitimationNumber());
                }
            }
            if ((member.getLicenseNumber() != null)) {
                if (memberRepository.findByLicenseNumber(member.getLicenseNumber()).isPresent()) {
                    System.out.println("Już ktoś ma ten numer licencji");
                    return false;
                } else {
                    memberEntity.setLicenseNumber(member.getLicenseNumber());
                    System.out.println(goodMessage() + "Numer Licencji");
                }
            }
            if (member.getShootingPatentNumber() != null) {
                if (memberRepository.findByShootingPatentNumber(member.getShootingPatentNumber()).isPresent()) {
                    System.out.println("Już ktoś ma ten numer patentu");
                    return false;
                } else {
                    memberEntity.setShootingPatentNumber(member.getShootingPatentNumber());
                    System.out.println(goodMessage() + "Numer Patentu");
                }
            }
            if (member.getEmail() != null) {
                if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
                    System.out.println("Już ktoś ma taki sam e-mail");
                    return false;
                } else {
                    memberEntity.setEmail(member.getEmail());
                    System.out.println(goodMessage() + "Email");
                }
            }
            if (member.getPesel() != null) {
                if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
                    System.out.println("Już ktoś ma taki sam numer PESEL");
                    return false;
                } else {
                    memberEntity.setPesel(member.getPesel());
                    System.out.println(goodMessage() + "Numer PESEL");
                }
            }
            if (member.getAddress() != null) {
                memberEntity.setAddress(member.getAddress());
                System.out.println(goodMessage() + "Adres");

            }
            memberRepository.saveAndFlush(memberEntity);
            return true;
        } catch (
                EntityNotFoundException ex) {
            badMessage();
            return false;
        }
    }

    private String goodMessage() {
        return "Zaktualizowano pomyślnie : ";
    }

    private void badMessage() {
        System.out.println("Nie udało się zaktualizować bo Klubowicz nie istnieje");
    }

    //--------------------------------------------------------------------------

    // Mapping
    private Member map(MemberEntity e) {
        return Member.builder()
                .joinDate(e.getJoinDate())
                .legitimationNumber(e.getLegitimationNumber())
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .licenseNumber(e.getLicenseNumber())
                .shootingPatentNumber(e.getShootingPatentNumber())
                .email(e.getEmail())
                .pesel(e.getPesel())
                .address(e.getAddress())
                .build();
    }

    private MemberEntity map(Member e) {
        return MemberEntity.builder()
                .joinDate(e.getJoinDate())
                .legitimationNumber(e.getLegitimationNumber())
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .licenseNumber(e.getLicenseNumber())
                .shootingPatentNumber(e.getShootingPatentNumber())
                .email(e.getEmail())
                .pesel(e.getPesel())
                .address(e.getAddress())
                .build();
    }
}
