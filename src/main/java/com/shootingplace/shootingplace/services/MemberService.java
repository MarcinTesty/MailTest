package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.enums.ArbiterClass;
import com.shootingplace.shootingplace.domain.models.History;
import com.shootingplace.shootingplace.domain.models.Member;
import com.shootingplace.shootingplace.domain.models.WeaponPermission;
import com.shootingplace.shootingplace.repositories.*;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;
    private final LicenseRepository licenseRepository;
    private final ShootingPatentRepository shootingPatentRepository;
    private final ContributionService contributionService;
    private final HistoryService historyService;
    private final WeaponPermissionService weaponPermissionService;
    private final WeaponPermissionRepository weaponPermissionRepository;
    private final MemberPermissionsRepository memberPermissionsRepository;
    private final PersonalEvidenceRepository personalEvidenceRepository;
    private final FilesService filesService;
    private final ClubRepository clubRepository;
    private final Logger LOG = LogManager.getLogger();


    public MemberService(MemberRepository memberRepository,
                         AddressRepository addressRepository,
                         LicenseRepository licenseRepository,
                         ShootingPatentRepository shootingPatentRepository, ContributionService contributionService,
                         HistoryService historyService, WeaponPermissionService weaponPermissionService, WeaponPermissionRepository weaponPermissionRepository, MemberPermissionsService memberPermissionsService, MemberPermissionsRepository memberPermissionsRepository, PersonalEvidenceService personalEvidenceService, PersonalEvidenceRepository personalEvidenceRepository, FilesService filesService, ClubRepository clubRepository) {
        this.memberRepository = memberRepository;
        this.addressRepository = addressRepository;
        this.licenseRepository = licenseRepository;
        this.shootingPatentRepository = shootingPatentRepository;
        this.contributionService = contributionService;
        this.historyService = historyService;
        this.weaponPermissionService = weaponPermissionService;
        this.weaponPermissionRepository = weaponPermissionRepository;
        this.memberPermissionsRepository = memberPermissionsRepository;
        this.personalEvidenceRepository = personalEvidenceRepository;
        this.filesService = filesService;
        this.clubRepository = clubRepository;
    }


    //--------------------------------------------------------------------------
    public List<MemberEntity> getMembersList(Boolean active, Boolean adult, Boolean erase) {

        List<MemberEntity> list = new ArrayList<>(memberRepository.findAllByActiveAndAdultAndErased(active, adult, erase));
        String c = "aktywnych";
        if (!active) {
            c = "nieaktywnych";
        }
        LOG.info("wyświetlono listę osób " + c);
        LOG.info("ilość osób " + c + " : " + list.size());
        list.sort(Comparator.comparing(MemberEntity::getSecondName));
        return list;
    }

    public List<Member> getMembersWithPermissions() {
        List<Member> list = new ArrayList<>();

        memberRepository.findAll().forEach(e -> {
            if ((e.getMemberPermissions().getShootingLeaderNumber() != null)
                    || (e.getMemberPermissions().getArbiterNumber() != null)
                    || (e.getMemberPermissions().getInstructorNumber() != null)) {
                list.add(Mapping.map(e));
            }
        });
        list.sort(Comparator.comparing(Member::getSecondName));
        LOG.info("Wywołano listę osób z uprawnieniami");
        return list;
    }

    public List<String> getArbiters() {
        List<String> list = new ArrayList<>();
        memberRepository.findAll().stream()
                .filter(e -> e.getMemberPermissions().getArbiterNumber() != null)
                .forEach(e -> list.add(e.getSecondName().concat(" " + e.getFirstName() + " " + e.getMemberPermissions().getArbiterClass() + " leg. " + e.getLegitimationNumber())));
        list.sort(Comparator.comparing(String::new));
        return list;
    }

    public List<String> getMembersNameAndLegitimationNumber(Boolean active, Boolean adult, Boolean erase) {

        // dorośli
        List<MemberEntity> adultMembers = memberRepository
                .findAll()
                .stream()
                .filter(MemberEntity::getAdult)
                .filter(MemberEntity::getActive)
                .collect(Collectors.toList());
        // nie ma żadnych składek
        adultMembers.forEach(e -> {
            if (e.getHistory().getContributionList().isEmpty() || e.getHistory().getContributionList() == null) {
                e.setActive(false);
                memberRepository.saveAndFlush(e);
            }
            //dzisiejsza data jest później niż składka + 3 miesiące
            else {
                if (e.getHistory().getContributionList().get(0).getValidThru().plusMonths(3).isBefore(LocalDate.now())) {
                    e.setActive(false);
                    LOG.info("zmieniono " + e.getSecondName());
                    memberRepository.saveAndFlush(e);

                }
            }
        });
        //młodzież
        List<MemberEntity> nonAdultMembers = memberRepository.findAll().stream().filter(f -> !f.getAdult()).filter(MemberEntity::getActive).collect(Collectors.toList());
        // nie ma żadnych składek
        nonAdultMembers.forEach(e -> {
            if (e.getHistory().getContributionList().isEmpty() || e.getHistory().getContributionList() == null) {
                e.setActive(false);
                memberRepository.saveAndFlush(e);
            } else {
                //dzisiejsza data jest później niż składka + 3 miesiące
                if (e.getHistory().getContributionList().get(0).getValidThru().plusMonths(3).isBefore(LocalDate.now())) {
                    e.setActive(false);
                    LOG.info("zmieniono " + e.getSecondName());
                    memberRepository.saveAndFlush(e);

                }
            }
        });

        List<String> list = new ArrayList<>();
        memberRepository.findAllByActiveAndAdultAndErased(active, adult, erase)
                .forEach(e ->
                        list.add(e.getSecondName().concat(" " + e.getFirstName() + " leg. " + e.getLegitimationNumber())));
        list.sort(Comparator.comparing(String::new));
        LOG.info("Lista nazwisk z identyfikatorem");
        return list;
    }

    //--------------------------------------------------------------------------
    public ResponseEntity<?> addNewMember(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setActive(true);

        List<MemberEntity> memberEntityList = memberRepository.findAll();

        if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
            LOG.error("Ktoś już ma taki numer PESEL");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer PESEL\"");
        }
        if (member.getEmail() == null || member.getEmail().isEmpty()) {
            memberEntity.setEmail("");
        }
        memberEntity.setEmail(member.getEmail().toLowerCase());
        if (memberEntityList.stream().filter(f -> f.getEmail().equals(" ") || f.getEmail() == null).anyMatch(e -> e.getEmail().equals(member.getEmail()))) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki e-mail\"" + member.getEmail());
        }
        if (memberEntityList.stream().anyMatch(e -> e.getLegitimationNumber().equals(member.getLegitimationNumber()))) {
            LOG.error("Ktoś już ma taki numer legitymacji");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer legitymacji\"");
        }
        if (memberEntityList.stream().anyMatch(e -> "+48".concat(e.getPhoneNumber().replaceAll("\\s", "")).equals(member.getPhoneNumber()))) {
            LOG.error("Ktoś już ma taki numer telefonu");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer telefonu\"");
        }
        if (memberEntityList.stream().anyMatch(e -> e.getIDCard().trim().toUpperCase().equals(member.getIDCard()))) {
            LOG.error("Ktoś już ma taki numer dowodu osobistego");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer dowodu osobistego\"");
        } else {
            if (member.getJoinDate() == null) {
                memberEntity.setJoinDate(LocalDate.now());
                LOG.info("ustawiono domyślną datę zapisu " + memberEntity.getJoinDate());
            }
            else {
                memberEntity.setJoinDate(member.getJoinDate());
                LOG.info("ustawiono datę zapisu na w" + memberEntity.getJoinDate());
            }
            if (member.getLegitimationNumber() == null) {
                int number = 1;
                if (memberEntityList.isEmpty()) {
                    memberEntity.setLegitimationNumber(1);
                    number = 1;
                } else {
                    memberEntityList.sort(Comparator.comparing(MemberEntity::getLegitimationNumber));
                    Collections.reverse(memberEntityList);
                    number = memberEntityList.get(0).getLegitimationNumber() + 1;
                }
                if (memberEntityList.stream().anyMatch(e -> e.getLegitimationNumber().equals(member.getLegitimationNumber()))) {
                    LOG.error("Ktoś już ma taki numer legitymacji");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer legitymacji\"");
                } else {
                    memberEntity.setLegitimationNumber(number);
                    LOG.info("ustawiono domyślny numer legitymacji : " + memberEntity.getLegitimationNumber());
                }
            }
            else {
                memberEntity.setLegitimationNumber(member.getLegitimationNumber());
            }
            String s = "+48";
            if (member.getPhoneNumber() != null) {
                memberEntity.setPhoneNumber(s + member.getPhoneNumber().replaceAll("\\s", ""));
            }
            if (!member.getAdult()) {
                LOG.info("Klubowicz należy do młodzieży");
                memberEntity.setAdult(false);
            } else {
                LOG.info("Klubowicz należy do grupy dorosłej");
            }
            String[] s1 = member.getFirstName().split(" ");
            StringBuilder firstNames = new StringBuilder();
            for (String value : s1) {
                String splinted = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase() + " ";
                firstNames.append(splinted);
            }
            memberEntity.setFirstName(firstNames.toString());
            memberEntity.setSecondName(member.getSecondName().toUpperCase());
            LOG.info("Dodano nowego członka Klubu " + member.getFirstName());
            AddressEntity address = AddressEntity.builder()
                    .zipCode(null)
                    .postOfficeCity(null)
                    .street(null)
                    .streetNumber(null)
                    .flatNumber(null)
                    .build();
            addressRepository.save(address);
            memberEntity.setAddress(address);
            LicenseEntity license = LicenseEntity.builder()
                    .number(null)
                    .validThru(null)
                    .pistolPermission(false)
                    .riflePermission(false)
                    .shotgunPermission(false)
                    .valid(false)
                    .canProlong(false)
                    .paid(false)
                    .build();
            licenseRepository.save(license);
            ShootingPatentEntity shootingPatent = ShootingPatentEntity.builder()
                    .patentNumber(null)
                    .dateOfPosting(null)
                    .pistolPermission(false)
                    .riflePermission(false)
                    .shotgunPermission(false)
                    .build();
            shootingPatentRepository.save(shootingPatent);

            History history = History.builder()
                    .licenseHistory(new String[]{})
                    .patentDay(new LocalDate[3])
                    .licensePaymentHistory(null)
                    .patentFirstRecord(false).build();
            HistoryEntity historyEntity = historyService.createHistory(history);


            WeaponPermissionEntity weaponPermission = WeaponPermissionEntity.builder()
                    .number(null)
                    .isExist(false)
                    .build();
            weaponPermissionRepository.save(weaponPermission);

            MemberPermissionsEntity memberPermissions = MemberPermissionsEntity.builder()
                    .instructorNumber(null)
                    .shootingLeaderNumber(null)
                    .arbiterClass(ArbiterClass.NONE.getName())
                    .arbiterNumber(null)
                    .arbiterPermissionValidThru(null)
                    .build();
            memberPermissionsRepository.save(memberPermissions);
            PersonalEvidenceEntity personalEvidence = PersonalEvidenceEntity.builder()
                    .ammoList(new ArrayList<>())
                    .build();
            personalEvidenceRepository.save(personalEvidence);

            memberEntity.setIDCard(member.getIDCard().trim().toUpperCase());
            memberEntity.setPesel(member.getPesel());
            memberEntity.setErasedReason(null);
            memberEntity.setClub(clubRepository.findById(1).orElseThrow(EntityNotFoundException::new));
            memberEntity.setShootingPatent(shootingPatent);
            memberEntity.setLicense(license);
            memberEntity.setHistory(historyEntity);
            memberEntity.setWeaponPermission(weaponPermission);
            memberEntity.setMemberPermissions(memberPermissions);
            memberEntity.setPersonalEvidence(personalEvidence);
            memberEntity = memberRepository.save(memberEntity);
            ContributionEntity contributionEntity = contributionService.addFirstContribution(memberEntity.getUuid(), LocalDate.now());
            historyService.addContribution(memberEntity.getUuid(), contributionEntity);

        }
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body("\"" + memberEntity.getUuid() + "\"");


    }


    //--------------------------------------------------------------------------

    public boolean deleteMember(String uuid) {
        MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        if (memberRepository.existsById(uuid) && !memberEntity.getActive()) {
            memberRepository.deleteById(uuid);
            LOG.info("Usunięto członka klubu");
            return true;
        } else if (memberRepository.existsById(uuid) && memberEntity.getActive()) {
            LOG.warn("Klubowicz jest aktywny");
            return false;
        } else
            LOG.error("Nie znaleziono takiego klubowicza");
        return false;
    }

    //--------------------------------------------------------------------------
    // @Patch
    public ResponseEntity<?> activateOrDeactivateMember(String memberUUID) {
        if (!memberRepository.existsById(memberUUID)) {
            LOG.info("Nie znaleziono Klubowicza");
            return ResponseEntity.notFound().build();
        }
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        memberEntity.toggleActive();
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Zmieniono status");

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> changeAdult(String memberUUID) {
        if (!memberRepository.existsById(memberUUID)) {
            LOG.info("Nie znaleziono Klubowicza");
            return ResponseEntity.notFound().build();
        }
        if (memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new).getAdult()) {
            LOG.info("Klubowicz należy już do grupy powszechnej");
            return ResponseEntity.badRequest().build();
        }
        memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new).toggleAdult();
        LOG.info("Klubowicz należy od teraz do grupy dorosłej : " + LocalDate.now());

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> eraseMember(String memberUUID, String reason) {
        if (!memberRepository.existsById(memberUUID)) {
            LOG.info("Nie znaleziono Klubowicza");
            return ResponseEntity.notFound().build();
        }
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (!memberEntity.getErased()) {
            memberEntity.setErasedReason(reason);
            memberEntity.toggleErase();
            LOG.info("Klubowicz skreślony : " + LocalDate.now());
        } else {
            memberEntity.setErasedReason(null);
            memberEntity.toggleErase();
            LOG.info("Klubowicz przywrócony : " + LocalDate.now());
        }
        memberRepository.saveAndFlush(memberEntity);
        return ResponseEntity.noContent().build();
    }

    //--------------------------------------------------------------------------
    @SneakyThrows
    public ResponseEntity<?> updateMember(String memberUUID, Member member) {

        if (!memberRepository.existsById(memberUUID)) {
            LOG.info("Nie znaleziono Klubowicza");
            return ResponseEntity.notFound().build();
        }

        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        if (member.getFirstName() != null && !member.getFirstName().isEmpty()) {
            String[] s1 = member.getFirstName().split(" ");
            StringBuilder firstNames = new StringBuilder();
            for (String value : s1) {
                String splinted = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase() + " ";
                firstNames.append(splinted);
            }
            memberEntity.setFirstName(firstNames.toString());
            LOG.info(goodMessage() + "Imię");
        }
        if (member.getSecondName() != null && !member.getSecondName().isEmpty()) {
            memberEntity.setSecondName(member.getSecondName().toUpperCase());
            LOG.info(goodMessage() + "Nazwisko");

        }
        if (member.getJoinDate() != null) {
            memberEntity.setJoinDate(member.getJoinDate());
            LOG.info(goodMessage() + "Data przystąpienia do klubu");
        }
        if (member.getLegitimationNumber() != null) {
            if (memberRepository.findByLegitimationNumber(member.getLegitimationNumber()).isPresent()) {
                LOG.warn("Już ktoś ma taki numer legitymacji");
            } else {
                memberEntity.setLegitimationNumber(member.getLegitimationNumber());
                LOG.info(goodMessage() + "numer legitymacji");
            }
        }
        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
                LOG.error("Już ktoś ma taki sam e-mail");
            } else {
                memberEntity.setEmail(member.getEmail().trim().toLowerCase());
                LOG.info(goodMessage() + "Email");
            }
        }
        if (member.getPesel() != null && !member.getPesel().isEmpty()) {
            if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
                LOG.error("Już ktoś ma taki sam numer PESEL");
            } else {
                memberEntity.setPesel(member.getPesel());
                LOG.info(goodMessage() + "Numer PESEL");
            }
        }
        if (member.getPhoneNumber() != null && !member.getPhoneNumber().isEmpty()) {
            if (member.getPhoneNumber().replaceAll("\\s-", "").length() != 9 && !member.getPhoneNumber().isEmpty()) {
                LOG.error("Żle podany numer");
            }
            String s = "+48";
            memberEntity.setPhoneNumber((s + member.getPhoneNumber()).replaceAll("\\s", ""));
            if (memberRepository.findByPhoneNumber((s + member.getPhoneNumber()).replaceAll("\\s", "")).isPresent()) {
                LOG.error("Ktoś już ma taki numer telefonu");
            }
            if (member.getPhoneNumber().equals(memberEntity.getPhoneNumber())) {
                memberEntity.setPhoneNumber(member.getPhoneNumber());
                LOG.info(goodMessage() + "Numer Telefonu");
            }
        }
        if (member.getIDCard() != null && !member.getIDCard().isEmpty()) {
            if (memberRepository.findByIDCard(member.getIDCard().trim()).isPresent()) {
                LOG.error("Ktoś już ma taki numer dowodu");
            } else {
                memberEntity.setIDCard(member.getIDCard().trim().toUpperCase());
                LOG.info(goodMessage() + "Numer Dowodu");
            }
        }

        memberRepository.saveAndFlush(memberEntity);
        filesService.personalCardFile(memberEntity.getUuid());

        return ResponseEntity.ok().build();
    }

    public boolean changeWeaponPermission(String memberUUID, WeaponPermission weaponPermission) {

        return weaponPermissionService.updateWeaponPermission(memberUUID, weaponPermission);
    }

    private String goodMessage() {
        return "Zaktualizowano pomyślnie : ";
    }


    public MemberEntity getMember(int number) {
        LOG.info("Wywołano Klubowicza");
        return memberRepository.findAll().stream().filter(f -> f.getLegitimationNumber().equals(number)).findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public List<MemberEntity> getErasedMembers() {
        LOG.info("Wyświetlono osoby skreślone z listy członków");
        return memberRepository.findAllByErasedIsTrue();
    }

    public String getAdultMembersEmails(Boolean condition) {
        List<String> list = new ArrayList<>();
        memberRepository.findAll().forEach(e -> {
            if ((e.getEmail() != null && !e.getEmail().isEmpty()) && e.getAdult() == condition) {
                list.add(e.getEmail().concat(";"));
            }
        });
        return list.toString().replaceAll(",", "");
    }


    public ResponseEntity<?> updateJoinDate(String memberUUID, String date) {

        if (!memberRepository.existsById(memberUUID)) {
            LOG.info("Nie znaleziono Klubowicza");
            return ResponseEntity.notFound().build();
        }
        MemberEntity toUpdate = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LocalDate newDate = LocalDate.parse(date);
        toUpdate.setJoinDate(newDate);
        memberRepository.saveAndFlush(toUpdate);
        LOG.info(goodMessage() + "Data przystąpienia do klubu");
        return ResponseEntity.ok().build();

    }

    public String hardDelete(String memberUUID) {
        if (!memberRepository.existsById(memberUUID)) {
            return "Brak takiego Klubowicza";
        }
        memberRepository.deleteById(memberUUID);
        return "Usunięto";
    }

    public List<String> getMembersWithLicense(Boolean license) {

        List<String> list = new ArrayList<>();
        if (license) {
            memberRepository.findAll()
                    .stream()
                    .filter(e -> e.getLicense().getNumber() != null && e.getLicense().isValid())
                    .forEach(e -> list.add(e.getFirstName() + " " + e.getSecondName() + " " + e.getLicense().getNumber()));
        } else {
            memberRepository.findAll()
                    .stream()
                    .filter(e -> e.getLicense().getNumber() != null && !e.getLicense().isValid())
                    .forEach(e -> list.add(e.getFirstName() + " " + e.getSecondName() + " " + e.getLicense().getNumber()));
        }
        return list;
    }

    public List<String> getAllActiveMembersNames() {
        List<String> list = new ArrayList<>();
        memberRepository.findAll().stream().filter(MemberEntity::getActive)
                .forEach(e ->
                        list.add(e.getSecondName().concat(" " + e.getFirstName() + " leg. " + e.getLegitimationNumber())));
        list.sort(Comparator.comparing(String::new));
        LOG.info("Lista nazwisk z identyfikatorem");
        return list;
    }
}
