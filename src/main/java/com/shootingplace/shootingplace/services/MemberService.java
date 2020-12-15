package com.shootingplace.shootingplace.services;

import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.enums.ArbiterClass;
import com.shootingplace.shootingplace.domain.models.*;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AddressService addressService;
    private final LicenseService licenseService;
    private final ShootingPatentService shootingPatentService;
    private final ContributionService contributionService;
    private final HistoryService historyService;
    private final WeaponPermissionService weaponPermissionService;
    private final MemberPermissionsService memberPermissionsService;
    private final PersonalEvidenceService personalEvidenceService;
    private final FilesService filesService;
    private final Logger LOG = LogManager.getLogger();


    public MemberService(MemberRepository memberRepository,
                         AddressService addressService,
                         LicenseService licenseService,
                         ShootingPatentService shootingPatentService,
                         ContributionService contributionService,
                         HistoryService historyService, WeaponPermissionService weaponPermissionService, MemberPermissionsService memberPermissionsService, PersonalEvidenceService personalEvidenceService, FilesService filesService) {
        this.memberRepository = memberRepository;
        this.contributionService = contributionService;
        this.addressService = addressService;
        this.licenseService = licenseService;
        this.shootingPatentService = shootingPatentService;
        this.historyService = historyService;
        this.weaponPermissionService = weaponPermissionService;
        this.memberPermissionsService = memberPermissionsService;
        this.personalEvidenceService = personalEvidenceService;
        this.filesService = filesService;
    }


    //--------------------------------------------------------------------------
    public Map<UUID, Member> getMembers() {
        Map<UUID, Member> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> map.put(e.getUuid(), Mapping.map(e)));
        LOG.info("Wyświetlono listę członków klubu");
        LOG.info("Ilość klubowiczów aktywnych : " + memberRepository.findAllByActive(true).size());
        LOG.info("Ilość klubowiczów nieaktywnych : " + memberRepository.findAllByActive(false).size());
        LOG.info("liczba wpisów do rejestru : " + map.size());
        return map;
    }

    public List<MemberEntity> getMembersList(Boolean active, Boolean adult, Boolean erase) {
        memberRepository.findAll().forEach(e -> {
            if ((e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 9, 30))
                    || e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 3, 31)))
                    && e.getActive()) {
                e.setActive(false);
                memberRepository.save(e);
                LOG.info("sprawdzono i zmieniono status " + e.getFirstName() + " " + e.getSecondName() + " na Nieaktywny");
            } else if ((e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 9, 30))
                    || e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 3, 31)))
                    && !e.getActive()) {
                e.setActive(true);
                memberRepository.save(e);
                LOG.info("sprawdzono i zmieniono status " + e.getFirstName() + " " + e.getSecondName() + " na Aktywny");
            }
            if (e.getLicense().getValidThru() != null) {
                if (e.getLicense().getValidThru().isBefore(LocalDate.now())) {
                    e.getLicense().setValid(false);
                    licenseService.updateLicense(e.getUuid(), Mapping.map(e.getLicense()));
                    LOG.info("sprawdzono i zmieniono status licencji " + e.getFirstName() + " " + e.getSecondName() + " na nieważną");
                }
            }
//            reset startów po nowym roku
            LocalDate date = LocalDate.of(2020, 12, 31);
            if (LocalDate.now().isAfter(date)) {
                e.getHistory().setPistolCounter(0);
                e.getHistory().setRifleCounter(0);
                e.getHistory().setShotgunCounter(0);
//                date = LocalDate.of(LocalDate.now().getYear(), 12, 31);
                LOG.info("zresetowano licznik zawodów");

            }
        });
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
        return list;
    }

    public List<String> getArbiters(){
        List<String> list = new ArrayList<>();
        memberRepository.findAll().stream()
                .filter(e-> e.getMemberPermissions().getArbiterNumber()!= null)
                .forEach(e-> list.add(e.getSecondName().concat(" " + e.getFirstName() + " " + e.getUuid())));
        list.sort(Comparator.comparing(String::new));
        return list;
    }

    public List<String> getMembersNameAndUUID(Boolean active, Boolean adult, Boolean erase) {
        List<String> list = new ArrayList<>();
        memberRepository.findAllByActiveAndAdultAndErased(active, adult, erase)
                .forEach(e ->
                        list.add(e.getSecondName().concat(" " + e.getFirstName() + " " + e.getUuid())));
        list.sort(Comparator.comparing(String::new));
        LOG.info("Lista nazwisk z identyfikatorem");
        return list;
    }

    //--------------------------------------------------------------------------
    public ResponseEntity<?> addNewMember(Member member) throws IOException, DocumentException {
        MemberEntity memberEntity;
        member.setActive(true);
        if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
            LOG.error("Ktoś już ma taki numer PESEL");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer PESEL\"");
        }
        if (member.getEmail() == null || member.getEmail().isEmpty()) {
            member.setEmail("");
        }
//        if (memberRepository.findByEmail(member.getEmail()).isPresent() && !member.getEmail().isEmpty()) {
//            LOG.error("Ktoś już ma taki adres e-mail");
//        throw new Exception();
//        }
        if (memberRepository.findByLegitimationNumber(member.getLegitimationNumber()).isPresent()) {
            LOG.error("Ktoś już ma taki numer legitymacji");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer legitymacji\"");
        }
        if (memberRepository.findByPhoneNumber("+48".concat(member.getPhoneNumber())).isPresent()) {
            LOG.error("Ktoś już ma taki numer telefonu");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer telefonu\"");
        }
        if (memberRepository.findByIDCard(member.getIDCard()).isPresent()) {
            LOG.error("Ktoś już ma taki numer dowodu osobistego");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer dowodu osobistego\"");
        } else {
            member.setIDCard(member.getIDCard().toUpperCase());
            if (member.getJoinDate() == null) {
                member.setJoinDate(LocalDate.now());
                LOG.info("ustawiono domyślną datę zapisu " + member.getJoinDate());
            }
            if (member.getLegitimationNumber() == null) {
                List<MemberEntity> numberList = new ArrayList<>(memberRepository.findAll());
                Integer number;
                if (numberList.isEmpty()) {
                    member.setLegitimationNumber(1);
                    number = 1;
                } else {
                    numberList.sort(Comparator.comparing(MemberEntity::getLegitimationNumber));
                    Collections.reverse(numberList);
                    number = numberList.get(0).getLegitimationNumber() + 1;
                }
                if (memberRepository.findByLegitimationNumber(number).isPresent()) {
                    LOG.error("Ktoś już ma taki numer legitymacji");
                    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("\"Uwaga! Ktoś już ma taki numer legitymacji\"");
                } else {
                    member.setLegitimationNumber(number);
                    LOG.info("ustawiono domyślny numer legitymacji : " + member.getLegitimationNumber());
                }
            }
            String s = "+48";
            if (member.getPhoneNumber() != null) {
                member.setPhoneNumber(s + member.getPhoneNumber().replaceAll("\\s", ""));
            }
            if (!member.getAdult()) {
                LOG.info("Klubowicz należy do młodzieży");
                member.setAdult(false);
            } else {
                LOG.info("Klubowicz należy do grupy dorosłej");
            }
            member.setFirstName(member.getFirstName().substring(0, 1).toUpperCase() + member.getFirstName().substring(1).toLowerCase());
            member.setSecondName(member.getSecondName().toUpperCase());
            LOG.info("Dodano nowego członka Klubu " + member.getFirstName());
            memberEntity = memberRepository.saveAndFlush(Mapping.map(member));
            if (memberEntity.getAddress() == null) {
                Address address = Address.builder()
                        .zipCode(null)
                        .postOfficeCity(null)
                        .street(null)
                        .streetNumber(null)
                        .flatNumber(null)
                        .build();
                addressService.addAddress(memberEntity.getUuid(), address);
            }
            if (memberEntity.getLicense() == null) {
                License license = License.builder()
                        .number(null)
                        .validThru(null)
                        .pistolPermission(false)
                        .riflePermission(false)
                        .shotgunPermission(false)
                        .isValid(false)
                        .canProlong(false)
                        .club("Klub Strzelecki Dziesiątka LOK Łódź")
                        .build();
                licenseService.addLicenseToMember(memberEntity.getUuid(), license);
            }
            if (memberEntity.getShootingPatent() == null) {
                ShootingPatent shootingPatent = ShootingPatent.builder()
                        .patentNumber(null)
                        .dateOfPosting(null)
                        .pistolPermission(false)
                        .riflePermission(false)
                        .shotgunPermission(false)
                        .build();
                shootingPatentService.addPatent(memberEntity.getUuid(), shootingPatent);
            }
            if (memberEntity.getContribution() == null) {
                LocalDate localDate = LocalDate.now();
                int year = LocalDate.now().getYear();
                if (localDate.isBefore(LocalDate.of(year, 6, 30))) {
                    localDate = LocalDate.of(year, 6, 30);
                } else {
                    localDate = LocalDate.of(year, 12, 31);
                }
                Contribution contribution = Contribution.builder()
                        .contribution(localDate)
                        .paymentDay(LocalDate.now())
                        .build();
                contributionService.addContribution(memberEntity.getUuid(), contribution);

            }
            if (memberEntity.getHistory() == null) {
                LocalDate localDate = LocalDate.now();
                History history = History.builder()
                        .contributionRecord(new LocalDate[]{localDate})
                        .licenseHistory(new String[]{})
                        .patentDay(new LocalDate[3])
                        .licensePaymentHistory(null)
                        .patentFirstRecord(false).build();
                historyService.createHistory(memberEntity.getUuid(), history);
            }
            if (memberEntity.getWeaponPermission() == null) {
                WeaponPermission weaponPermission = WeaponPermission.builder()
                        .number(null)
                        .isExist(false)
                        .build();
                weaponPermissionService.addWeaponPermission(memberEntity.getUuid(), weaponPermission);
            }
            if (memberEntity.getMemberPermissions() == null) {
                MemberPermissions memberPermissions = MemberPermissions.builder()
                        .instructorNumber(null)
                        .shootingLeaderNumber(null)
                        .arbiterClass(ArbiterClass.NONE.getName())
                        .arbiterNumber(null)
                        .arbiterPermissionValidThru(null)
                        .build();
                memberPermissionsService.addMemberPermissions(memberEntity.getUuid(), memberPermissions);
            }
            if (memberEntity.getContributionFile() == null) {
                FilesModel filesModel = FilesModel.builder()
                        .name("")
                        .data(null)
                        .type(String.valueOf(MediaType.APPLICATION_PDF))
                        .build();
                filesService.createContributionFileEntity(memberEntity.getUuid(), filesModel);
                filesService.contributionConfirm(memberEntity.getUuid());
            }
            if (memberEntity.getPersonalCardFile() == null) {
                FilesModel filesModel = FilesModel.builder()
                        .name("")
                        .data(null)
                        .type(String.valueOf(MediaType.APPLICATION_PDF))
                        .build();
                filesService.createPersonalCardFileEntity(memberEntity.getUuid(), filesModel);
                filesService.personalCardFile(memberEntity.getUuid());
            }
            if (memberEntity.getPersonalEvidence() == null) {
                PersonalEvidence personalEvidence = PersonalEvidence.builder()
                        .ammo(new String[0])
                        .file(null)
                        .build();
                personalEvidenceService.addPersonalEvidence(memberEntity.getUuid(), personalEvidence);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(memberEntity.getUuid());


    }


    //--------------------------------------------------------------------------

    public boolean deleteMember(UUID uuid) {
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
    public ResponseEntity<?> activateOrDeactivateMember(UUID memberUUID) {
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

    public ResponseEntity<?> changeAdult(UUID memberUUID) {
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

    public ResponseEntity<?> eraseMember(UUID memberUUID) {
        if (!memberRepository.existsById(memberUUID)) {
            LOG.info("Nie znaleziono Klubowicza");
            return ResponseEntity.notFound().build();
        }
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        memberEntity.toggleErase();
        if (memberEntity.getErased()) {
            LOG.info("Klubowicz skreślony : " + LocalDate.now());
        }
        if (!memberEntity.getErased()) {
            LOG.info("Klubowicz przywrócony : " + LocalDate.now());
        }
        memberRepository.saveAndFlush(memberEntity);
        return ResponseEntity.noContent().build();
    }


    //--------------------------------------------------------------------------
    @SneakyThrows
    public ResponseEntity<?> updateMember(UUID memberUUID, Member member) {

        if (!memberRepository.existsById(memberUUID)) {
            LOG.info("Nie znaleziono Klubowicza");
            return ResponseEntity.notFound().build();
        }

        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        if (member.getFirstName() != null && !member.getFirstName().isEmpty()) {
            memberEntity.setFirstName(member.getFirstName());
            LOG.info(goodMessage() + "Imię");
        }
        if (member.getSecondName() != null && !member.getSecondName().isEmpty()) {
            memberEntity.setSecondName(member.getSecondName());
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
                memberEntity.setEmail(member.getEmail().trim());
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
            if (memberRepository.findByIDCard(member.getIDCard()).isPresent()) {
                LOG.error("Ktoś już ma taki numer dowodu");
            }
            memberEntity.setIDCard(member.getIDCard().toUpperCase());
            LOG.info(goodMessage() + "Numer Dowodu");
        }

        memberRepository.saveAndFlush(memberEntity);
        filesService.personalCardFile(memberEntity.getUuid());

        return ResponseEntity.noContent().build();
    }

    public boolean changeWeaponPermission(UUID memberUUID, WeaponPermission weaponPermission) {

        return weaponPermissionService.updateWeaponPermission(memberUUID, weaponPermission);
    }

    private String goodMessage() {
        return "Zaktualizowano pomyślnie : ";
    }


    public MemberEntity getMember(UUID uuid) {
        LOG.info("Wywołano Klubowicza");
        return memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
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


    public List<String> getMembersNamesWithPermissions(Boolean arbiter) {

        List<String> list = new ArrayList<>();
        if (arbiter) {
            memberRepository.findAll().forEach(e -> {
                if (e.getMemberPermissions().getArbiterNumber() != null) {
                    list.add(e.getFirstName().concat(" " + e.getSecondName() + " " + e.getMemberPermissions().getArbiterClass() + " " + e.getUuid()));
                }
            });
        }
        return list;
    }

    public ResponseEntity<?> updateJoinDate(UUID memberUUID, String date) {

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

    public String hardDelete(UUID memberUUID) {
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
                    .filter(e -> e.getLicense().getNumber() != null && e.getLicense().getValid())
                    .forEach(e -> list.add(e.getFirstName() + " " + e.getSecondName() + " " + e.getLicense().getNumber()));
        } else {
            memberRepository.findAll()
                    .stream()
                    .filter(e -> e.getLicense().getNumber() != null && !e.getLicense().getValid())
                    .forEach(e -> list.add(e.getFirstName() + " " + e.getSecondName() + " " + e.getLicense().getNumber()));
        }
        return list;
    }
}
