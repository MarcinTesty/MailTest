package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.domain.models.MemberDTO;
import com.shootingplace.shootingplace.repositories.LicenseRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LicenseService {
    private final MemberRepository memberRepository;
    private final LicenseRepository licenseRepository;
    private final HistoryService historyService;
    private final Logger LOG = LogManager.getLogger(getClass());


    public LicenseService(MemberRepository memberRepository,
                          LicenseRepository licenseRepository, HistoryService historyService) {
        this.memberRepository = memberRepository;
        this.licenseRepository = licenseRepository;
        this.historyService = historyService;
    }

    public List<MemberDTO> getMembersNamesAndLicense() {
        List<MemberDTO> list = new ArrayList<>();
        memberRepository.findAll()
                .forEach(e -> {
                    if (e.getLicense().getNumber() != null) {
                        list.add(Mapping.map2(e));
                    }
                });
        list.sort(Comparator.comparing(MemberDTO::getSecondName).thenComparing(MemberDTO::getFirstName));
        LOG.info("Wysłano listę osób z licencjami");
        return list;
    }

    void addLicenseToMember(String memberUUID, License license) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getLicense() != null) {
            LOG.error("nie można już dodać licencji");
        }
        LicenseEntity licenseEntity = Mapping.map(license);
        licenseRepository.saveAndFlush(licenseEntity);
        memberEntity.setLicense(licenseEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Licencja została zapisana");
    }

    public boolean updateLicense(String memberUUID, License license) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LicenseEntity licenseEntity = licenseRepository.findById(memberEntity
                .getLicense()
                .getUuid())
                .orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getActive().equals(false)) {
            LOG.error("Klubowicz nie aktywny");
            return false;
        }
        if (memberEntity.getShootingPatent().getPatentNumber() == null && memberEntity.getAdult()) {
            LOG.error("Brak patentu");
            return false;
        }
        if (license.getNumber() != null
                && memberEntity.getLicense().getUuid() == licenseEntity.getUuid()) {
            if(licenseRepository.findAll()
                    .stream().filter(e-> !(e.getNumber() ==null))
                    .anyMatch(f->f.getNumber().equals(license.getNumber()))){
                LOG.error("Ktoś już ma taki numer licencji");
                return false;
            } else {
                licenseEntity.setNumber(license.getNumber());
                LOG.info("Dodano numer licencji");
            }
        }
        if (license.getPistolPermission() != null) {
            if (!memberEntity.getShootingPatent().getPistolPermission() && memberEntity.getAdult()) {
                LOG.error(noPatentMessage());
            }
            if (license.getPistolPermission().equals(true)) {
                historyService.addLicenseHistoryRecord(memberUUID, 0);
                licenseEntity.setPistolPermission(license.getPistolPermission());
                LOG.info("Dodano dyscyplinę : pistolet");
            }
        }
        if (license.getRiflePermission() != null) {
            if (!memberEntity.getShootingPatent().getRiflePermission() && memberEntity.getAdult()) {
                LOG.error(noPatentMessage());
            }
            if (license.getRiflePermission().equals(true)) {
                historyService.addLicenseHistoryRecord(memberUUID, 1);
                licenseEntity.setRiflePermission(license.getRiflePermission());
                LOG.info("Dodano dyscyplinę : karabin");
            }
        }
        if (license.getShotgunPermission() != null) {
            if (!memberEntity.getShootingPatent().getShotgunPermission() && memberEntity.getAdult()) {
                LOG.error(noPatentMessage());
            }
            if (license.getShotgunPermission().equals(true)) {
                historyService.addLicenseHistoryRecord(memberUUID, 2);
                licenseEntity.setShotgunPermission(license.getShotgunPermission());
                LOG.info("Dodano dyscyplinę : strzelba");
            }
        }
        if (license.getValidThru() != null) {
            licenseEntity.setValidThru(LocalDate.of(license.getValidThru().getYear(), 12, 31));
            if(LocalDate.now().getYear()<license.getValidThru().getYear()){
                licenseEntity.setValid(true);
            }
            LOG.info("zaktualizowano datę licencji");
        } else {
            licenseEntity.setValidThru(LocalDate.of(LocalDate.now().getYear(), 12, 31));
            licenseEntity.setValid(true);
            LOG.info("Brak ręcznego ustawienia daty, ustawiono na koniec bieżącego roku " + licenseEntity.getValidThru());
        }
        licenseEntity.setPaid(false);

        licenseRepository.saveAndFlush(licenseEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("zaktualizowano licencję");
        return true;
    }

    private String noPatentMessage() {
        return "Nie ma na to Patentu";
    }

    public boolean renewLicenseValid(String memberUUID, License license) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LicenseEntity licenseEntity = licenseRepository.findById(memberEntity.getLicense().getUuid()).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getActive()
                && licenseEntity.getNumber() != null && licenseEntity.isPaid()) {
            if (LocalDate.now().isAfter(LocalDate.of(licenseEntity.getValidThru().getYear(), 11, 1))) {
                licenseEntity.setValidThru(LocalDate.of((licenseEntity.getValidThru().getYear() + 1), 12, 31));
                licenseEntity.setValid(true);
                if (license.getPistolPermission() != null) {
                    if (!memberEntity.getShootingPatent().getPistolPermission() && memberEntity.getAdult()) {
                        LOG.error("Brak Patentu");
                    }
                    if (license.getPistolPermission() != null && memberEntity.getShootingPatent().getPistolPermission()) {
                        if (!license.getPistolPermission()) {
                            historyService.addLicenseHistoryRecord(memberUUID, 0);
                        }
                        licenseEntity.setPistolPermission(license.getPistolPermission());
                        LOG.info("Dodano dyscyplinę : pistolet");
                    }
                }
                if (license.getRiflePermission() != null) {
                    if (!memberEntity.getShootingPatent().getRiflePermission() && memberEntity.getAdult()) {
                        LOG.error("Brak Patentu");
                    }
                    if (license.getRiflePermission() != null && memberEntity.getShootingPatent().getRiflePermission()) {
                        if (!license.getRiflePermission()) {
                            historyService.addLicenseHistoryRecord(memberUUID, 1);
                        }
                        licenseEntity.setRiflePermission(license.getRiflePermission());
                        LOG.info("Dodano dyscyplinę : karabin");
                    }
                }
                if (license.getShotgunPermission() != null) {
                    if (!memberEntity.getShootingPatent().getShotgunPermission() && memberEntity.getAdult()) {
                        LOG.error("Brak Patentu");
                    }
                    if (license.getShotgunPermission() != null && memberEntity.getShootingPatent().getShotgunPermission()) {
                        if (!license.getShotgunPermission()) {
                            historyService.addLicenseHistoryRecord(memberUUID, 2);
                        }
                        licenseEntity.setShotgunPermission(license.getShotgunPermission());
                        LOG.info("Dodano dyscyplinę : strzelba");
                    }
                }
                licenseEntity.setCanProlong(false);
                licenseEntity.setPaid(false);
                memberEntity.getHistory().setPistolCounter(0);
                memberEntity.getHistory().setRifleCounter(0);
                memberEntity.getHistory().setShotgunCounter(0);
                licenseRepository.saveAndFlush(licenseEntity);
                LOG.info("Przedłużono licencję");
                return true;

            } else {
                LOG.error("nie można przedłużyć licencji");
                return false;
            }
        }
        return false;
    }
}