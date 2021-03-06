package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import com.shootingplace.shootingplace.domain.entities.LicensePaymentHistoryEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.domain.models.MemberDTO;
import com.shootingplace.shootingplace.repositories.LicensePaymentHistoryRepository;
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
import java.util.stream.Collectors;

@Service
public class LicenseService {
    private final MemberRepository memberRepository;
    private final LicenseRepository licenseRepository;
    private final HistoryService historyService;
    private final ChangeHistoryService changeHistoryService;
    private final LicensePaymentHistoryRepository licensePaymentHistoryRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public LicenseService(MemberRepository memberRepository,
                          LicenseRepository licenseRepository, HistoryService historyService, ChangeHistoryService changeHistoryService, LicensePaymentHistoryRepository licensePaymentHistoryRepository) {
        this.memberRepository = memberRepository;
        this.licenseRepository = licenseRepository;
        this.historyService = historyService;
        this.changeHistoryService = changeHistoryService;
        this.licensePaymentHistoryRepository = licensePaymentHistoryRepository;
    }

    public List<MemberDTO> getMembersNamesAndLicense() {
        List<MemberDTO> list = new ArrayList<>();
        memberRepository.findAll()
                .stream().filter(f -> !f.getErased())
                .forEach(e -> {
                    if (e.getLicense().getNumber() != null) {
                        if (e.getLicense().isValid()) {

                            list.add(Mapping.map2(e));
                        }
                    }
                });
        list.sort(Comparator.comparing(MemberDTO::getSecondName).thenComparing(MemberDTO::getFirstName));
        LOG.info("Wys??ano list?? os??b z licencjami");
        return list;
    }

    public List<MemberDTO> getMembersNamesAndLicenseNotValid() {
        List<MemberDTO> list = new ArrayList<>();
        memberRepository.findAll()
                .stream().filter(f -> !f.getErased())
                .forEach(e -> {
                    if (e.getLicense().getNumber() != null) {
                        if (!e.getLicense().isValid()) {

                            list.add(Mapping.map2(e));
                        }
                    }
                });
        list.sort(Comparator.comparing(MemberDTO::getSecondName).thenComparing(MemberDTO::getFirstName));
        LOG.info("Wys??ano list?? os??b z licencjami");
        return list;
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
                && memberEntity.getLicense().getUuid().equals(licenseEntity.getUuid())) {
            List<MemberEntity> collect = memberRepository.findAll()
                    .stream()
                    .filter(f -> !f.getErased())
                    .filter(f -> f.getLicense().getNumber() != null)
                    .filter(f -> f.getLicense().getNumber().equals(license.getNumber()))
                    .collect(Collectors.toList());
            if (collect.size() > 0 && !licenseEntity.getNumber().equals(license.getNumber())) {
                LOG.error("Kto?? ju?? ma taki numer licencji");
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
                LOG.info("Dodano dyscyplin?? : pistolet");
            }
        }
        if (license.getRiflePermission() != null) {
            if (!memberEntity.getShootingPatent().getRiflePermission() && memberEntity.getAdult()) {
                LOG.error(noPatentMessage());
            }
            if (license.getRiflePermission().equals(true)) {
                historyService.addLicenseHistoryRecord(memberUUID, 1);
                licenseEntity.setRiflePermission(license.getRiflePermission());
                LOG.info("Dodano dyscyplin?? : karabin");
            }
        }
        if (license.getShotgunPermission() != null) {
            if (!memberEntity.getShootingPatent().getShotgunPermission() && memberEntity.getAdult()) {
                LOG.error(noPatentMessage());
            }
            if (license.getShotgunPermission().equals(true)) {
                historyService.addLicenseHistoryRecord(memberUUID, 2);
                licenseEntity.setShotgunPermission(license.getShotgunPermission());
                LOG.info("Dodano dyscyplin?? : strzelba");
            }
        }
        if (license.getValidThru() != null) {
            licenseEntity.setValidThru(LocalDate.of(license.getValidThru().getYear(), 12, 31));
            if (license.getValidThru().getYear() >= LocalDate.now().getYear()) {
                licenseEntity.setValid(true);
            }
            LOG.info("zaktualizowano dat?? licencji");
        } else {
            licenseEntity.setValidThru(LocalDate.of(LocalDate.now().getYear(), 12, 31));
            licenseEntity.setValid(true);
            LOG.info("Brak r??cznego ustawienia daty, ustawiono na koniec bie????cego roku " + licenseEntity.getValidThru());
        }
        licenseEntity.setPaid(false);

        licenseRepository.saveAndFlush(licenseEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("zaktualizowano licencj??");
        return true;
    }

    public boolean updateLicense(String memberUUID, String number, LocalDate date, String pinCode) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LicenseEntity license = licenseRepository.findById(memberEntity.getLicense().getUuid()).orElseThrow(EntityNotFoundException::new);

        if (number != null && !number.isEmpty() && !number.equals("null")) {

            List<MemberEntity> collect = memberRepository.findAll()
                    .stream()
                    .filter(f -> !f.getErased())
                    .filter(f -> f.getLicense().getNumber() != null)
                    .filter(f -> f.getLicense().getNumber().equals(number))
                    .collect(Collectors.toList());

            if (collect.size() > 0 && !license.getNumber().equals(number)) {
                LOG.error("Kto?? ju?? ma taki numer licencji");
                return false;
            } else {
                license.setNumber(number);
                LOG.info("Dodano numer licencji");
            }

        }
        if (date != null) {
            license.setValidThru(date);
            license.setValid(license.getValidThru().getYear() >= LocalDate.now().getYear());
        }
        licenseRepository.saveAndFlush(license);
        changeHistoryService.addRecordToChangeHistory(pinCode, license.getClass().getSimpleName() + " updateLicense", memberEntity.getUuid());
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
            if (LocalDate.now().isAfter(LocalDate.of(licenseEntity.getValidThru().getYear(), 11, 1)) || licenseEntity.getValidThru().isBefore(LocalDate.now())) {
                licenseEntity.setValidThru(LocalDate.of((licenseEntity.getValidThru().getYear() + 1), 12, 31));
                licenseEntity.setValid(licenseEntity.getValidThru().getYear() >= LocalDate.now().getYear());
                if (license.getPistolPermission() != null) {
                    if (!memberEntity.getShootingPatent().getPistolPermission() && memberEntity.getAdult()) {
                        LOG.error("Brak Patentu");
                    }
                    if (license.getPistolPermission() != null && memberEntity.getShootingPatent().getPistolPermission()) {
                        if (!license.getPistolPermission()) {
                            historyService.addLicenseHistoryRecord(memberUUID, 0);
                        }
                        licenseEntity.setPistolPermission(license.getPistolPermission());
                        LOG.info("Dodano dyscyplin?? : pistolet");
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
                        LOG.info("Dodano dyscyplin?? : karabin");
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
                        LOG.info("Dodano dyscyplin?? : strzelba");
                    }
                }
                licenseEntity.setCanProlong(false);
                licenseEntity.setPaid(false);
                memberEntity.getHistory().setPistolCounter(0);
                memberEntity.getHistory().setRifleCounter(0);
                memberEntity.getHistory().setShotgunCounter(0);
                licenseRepository.saveAndFlush(licenseEntity);
                LOG.info("Przed??u??ono licencj??");
                return true;

            } else {
                LOG.error("nie mo??na przed??u??y?? licencji");
                return false;
            }
        } else {
            LOG.error("nie mo??na przed??u??y?? licencji");
            return false;
        }
    }

    public boolean updateLicensePayment(String memberUUID, String paymentUUID, LocalDate date, Integer year, String pinCode) {

        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LicensePaymentHistoryEntity licensePaymentHistoryEntity = memberEntity.getHistory().getLicensePaymentHistory().stream().filter(f -> f.getUuid().equals(paymentUUID)).findFirst().orElseThrow(EntityNotFoundException::new);

        if (date != null) {
            licensePaymentHistoryEntity.setDate(date);
        }
        if (year != null) {
            licensePaymentHistoryEntity.setValidForYear(year);
        }

        licensePaymentHistoryRepository.saveAndFlush(licensePaymentHistoryEntity);
        changeHistoryService.addRecordToChangeHistory(pinCode, licensePaymentHistoryEntity.getClass().getSimpleName() + " updateLicense", memberEntity.getUuid());

        return true;
    }

    public List<Long> getMembersQuantity() {

        long count2 = memberRepository.findAll().stream()
                .filter(MemberEntity::getErased)
                .filter(f -> f.getLicense().getNumber() != null)
                .filter(f -> !f.getLicense().isValid())
                .count();

        List<Long> list = new ArrayList<>();
        //jak wyszuka?? licencje tylko os??b NIE-skre??lonych?

        long count = licenseRepository.findAll().stream()
                .filter(f -> f.getNumber() != null)
                .filter(f -> !f.isValid())
                .count();
        long count1 = licenseRepository.findAll().stream()
                .filter(f -> f.getNumber() != null)
                .filter(LicenseEntity::isValid)
                .count();

        list.add(count - count2);
        list.add(count1);

        return list;
    }
}