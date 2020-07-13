package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.repositories.LicenseRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final Logger LOG = LogManager.getLogger(getClass());


    public LicenseService(MemberRepository memberRepository,
                          LicenseRepository licenseRepository) {
        this.memberRepository = memberRepository;
        this.licenseRepository = licenseRepository;
    }

    public Map<UUID, License> getLicense() {
        Map<UUID, License> map = new HashMap<>();
        licenseRepository.findAllByNumberIsNotNull().forEach(e -> map.put(e.getUuid(), Mapping.map(e)));
        LOG.info("liczba wpisów do rejestru : " + map.size());
        return map;
    }

    public Map<String, License> getMembersNamesAndLicense() {
        Map<String, License> map = new HashMap<>();
        memberRepository.findAll()
                .forEach(e -> {
                    if (e.getLicense().getNumber() != null) {
                        map.put(e.getFirstName().concat(" " + e.getSecondName()), Mapping.map(e.getLicense()));
                    }
                });
        return map;
    }


    public boolean addLicenseToMember(UUID memberUUID, License license) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getLicense() != null) {
            LOG.error("nie można już dodać licencji");
            return false;
        }
        LicenseEntity licenseEntity = Mapping.map(license);
        licenseRepository.saveAndFlush(licenseEntity);
        memberEntity.setLicense(licenseEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Licencja została zapisana");
        return true;
    }

    public boolean updateLicense(UUID memberUUID, License license) {
        try {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            LicenseEntity licenseEntity = licenseRepository.findById(memberEntity
                    .getLicense()
                    .getUuid())
                    .orElseThrow(EntityNotFoundException::new);
            if (memberEntity.getActive().equals(false)) {
                LOG.error("Klubowicz nie aktywny");
                return false;
            }
            if (memberEntity.getShootingPatent().getPatentNumber() == null&&memberEntity.getAdult()) {
                LOG.error("Brak patentu");
                return false;
            }
            if (license.getNumber() != null
                    && memberEntity.getLicense().getUuid() == licenseEntity.getUuid()) {
                if (licenseRepository.findByNumber(license.getNumber()).isPresent()
                        && !memberEntity.getLicense().getNumber().equals(license.getNumber())) {
                    LOG.error("Ktoś już ma taki numer licencji");
                    return false;
                } else {
                    licenseEntity.setNumber(license.getNumber());
                    LOG.info("Zaktualizowano numer licencji");
                }
            }
            if (license.getPistolPermission()) {
                if (!memberEntity.getShootingPatent().getPistolPermission()&&memberEntity.getAdult()) {
                    LOG.error(noPatentMessage());
                    return false;
                } else {
                    licenseEntity.setPistolPermission(license.getPistolPermission());
                    LOG.info("Zaktualizowano dyscyplinę : pistolet");
                }
            }
            if (license.getRiflePermission()) {
                if (!memberEntity.getShootingPatent().getRiflePermission()&&memberEntity.getAdult()) {
                    LOG.error(noPatentMessage());
                    return false;
                } else {
                    licenseEntity.setRiflePermission(license.getRiflePermission());
                    LOG.info("Zaktualizowano dyscyplinę : karabin");
                }
            }
            if (license.getShotgunPermission()) {
                if (!memberEntity.getShootingPatent().getShotgunPermission()&&memberEntity.getAdult()) {
                    LOG.error(noPatentMessage());
                    return false;
                } else {
                    licenseEntity.setShotgunPermission(license.getShotgunPermission());
                    LOG.info("Zaktualizowano dyscypliny : strzelba");
                }
            }
            if (license.getValidThru() != null) {
                licenseEntity.setValidThru(LocalDate.of(license.getValidThru().getYear(), 12, 31));
                LOG.info("zaktualizowano datę licencji");
            } else {
                licenseEntity.setValidThru(LocalDate.of(LocalDate.now().getYear(), 12, 31));
                licenseEntity.setIsValid(true);
                LOG.info("Brak ręcznego ustawienia daty, ustawiono na koniec bieżącego roku " + licenseEntity.getValidThru());
            }
            licenseRepository.saveAndFlush(licenseEntity);
            memberRepository.saveAndFlush(memberEntity);
            LOG.info("zaktualizowano licencję");
            return true;
        } catch (Exception ex) {
            LOG.error("Ktoś już ma taki numer licencji " + ex.getMessage());
            return false;
        }
    }

    private String noPatentMessage() {
        return "Nie ma na to Patentu";
    }

    //   tutaj trzeba poprawić warunki bo coś mi się nie zgadza tylko nie wiem jeszcze co.
    public boolean renewLicenseValid(UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LicenseEntity licenseEntity = licenseRepository.findById(memberEntity.getLicense().getUuid()).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getActive()
                && licenseEntity.getNumber() != null) {

            licenseEntity.setValidThru(LocalDate.of((LocalDate.now().getYear() + 1), 12, 31));
            licenseRepository.saveAndFlush(licenseEntity);
            LOG.info("Przedłużono licencję");
            return true;

        }
        LOG.error("nie można przedłużyć licencji");
        return false;

    }
}