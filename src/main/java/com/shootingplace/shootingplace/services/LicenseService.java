package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.repositories.LicenseRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LicenseService {
    private final MemberRepository memberRepository;
    private final LicenseRepository licenseRepository;

    public LicenseService(MemberRepository memberRepository, LicenseRepository licenseRepository) {
        this.memberRepository = memberRepository;
        this.licenseRepository = licenseRepository;
    }

    public Map<UUID, License> getLicense() {
        Map<UUID, License> map = new HashMap<>();
        licenseRepository.findAllByNumberIsNotNull().forEach(e -> map.put(e.getUuid(), Mapping.map(e)));
        System.out.println("liczba wpisów do rejestru : " + map.size());
        return map;
    }


    public boolean addLicenseToMember(UUID memberUUID, License license) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getLicense() != null) {
            System.out.println("nie można już dodać licencji");
            return false;
        }
        LicenseEntity licenseEntity = Mapping.map(license);
        licenseRepository.saveAndFlush(licenseEntity);
        memberEntity.setLicense(licenseEntity);
        memberRepository.saveAndFlush(memberEntity);
        System.out.println("Licencja została zapisana");
        return true;
    }

    public boolean updateLicense(UUID memberUUID, License license) {
        try {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            LicenseEntity licenseEntity = licenseRepository.findById(memberEntity.getLicense().getUuid()).orElseThrow(EntityNotFoundException::new);
            if (license.getNumber() != null) {
                if (licenseRepository.findByNumber(license.getNumber()).isPresent()) {
                    System.out.println("Ktoś już ma taki numer licencji");
                    return false;
                } else {
                    licenseEntity.setNumber(license.getNumber());
                    System.out.println("Zaktualizowano numer licencji");
                }
            }
            if (license.getDisciplines() != null) {
                licenseEntity.setDisciplines(license.getDisciplines());
                System.out.println(("Zaktualizowano dyscypliny"));
            }
            licenseRepository.saveAndFlush(licenseEntity);
            memberRepository.saveAndFlush(memberEntity);
            return true;
        } catch (Exception ex) {
            ex.getMessage();
            return false;
        }
    }

    public boolean setOrRenewLicenseValid(UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LicenseEntity licenseEntity = licenseRepository.findById(memberEntity.getLicense().getUuid()).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getActive() && licenseEntity.getNumber() != null) {
            if (!licenseEntity.getValidThrough().isEqual(LocalDate.of((LocalDate.now().getYear() + 1), 12, 31))
                    || LocalDate.now().isAfter(LocalDate.of(LocalDate.now().getYear(), 11, 1))) {

                licenseEntity.setValidThrough(LocalDate.of((LocalDate.now().getYear() + 1), 12, 31));
                licenseRepository.saveAndFlush(licenseEntity);
                System.out.println("Przedłużono licencję");
                return true;
            }
        }
        System.out.println("nie można przedłużyć licencji");
        return false;

    }
}