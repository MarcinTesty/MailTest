package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.*;
import com.shootingplace.shootingplace.domain.models.WeaponPermission;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
    private final Logger LOG = LogManager.getLogger();


    public MemberService(MemberRepository memberRepository,
                         AddressService addressService,
                         LicenseService licenseService,
                         ShootingPatentService shootingPatentService,
                         ContributionService contributionService,
                         HistoryService historyService, WeaponPermissionService weaponPermissionService) {
        this.memberRepository = memberRepository;
        this.contributionService = contributionService;
        this.addressService = addressService;
        this.licenseService = licenseService;
        this.shootingPatentService = shootingPatentService;
        this.historyService = historyService;
        this.weaponPermissionService = weaponPermissionService;
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

    public Map<UUID, Member> getActiveMembers() {
        Map<UUID, Member> map = new HashMap<>();
        memberRepository.findAllByActive(true).forEach(e -> map.put(e.getUuid(), Mapping.map(e)));
        LOG.info("Ilość klubowiczów aktywnych : " + map.size());

        return map;
    }

    public List<MemberEntity> getActiveMembersList(Boolean active, Boolean adult, Boolean erase) {
        memberRepository.findAll().forEach(e -> {
            if ((e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 9, 30))
                    || e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 3, 31)))
                    && e.getActive()) {
                activateOrDeactivateMember(e.getUuid());
                memberRepository.save(e);
                LOG.info("sprawdzono i zmieniono status " + e.getFirstName() + " " + e.getSecondName() + " na Nieaktywny");
            } else if ((e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 9, 30))
                    || e.getContribution().getContribution().isBefore(LocalDate.of(LocalDate.now().getYear(), 3, 31)))
                    && !e.getActive()) {
                activateOrDeactivateMember(e.getUuid());
                memberRepository.save(e);
                LOG.info("sprawdzono i zmieniono status " + e.getFirstName() + " " + e.getSecondName() + " na Aktywny");
            }
            if (e.getLicense().getValidThru() != null) {
                if (e.getLicense().getValidThru().isBefore(LocalDate.now())) {
                    e.getLicense().setIsValid(false);
                    licenseService.updateLicense(e.getUuid(), Mapping.map(e.getLicense()));
                    LOG.info("sprawdzono i zmieniono status licencji " + e.getFirstName() + " " + e.getSecondName() + " na nieważną");
                }
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

    public List<MemberEntity> getNonActiveMembers(Boolean active, Boolean erased) {
        List<MemberEntity> list = new ArrayList<>(memberRepository.findAllByActiveAndErased(active, erased));
        LOG.info("Ilość klubowiczów nieaktywnych : " + list.size());

        return list;
    }

    public Map<String, String> getMembersNamesWithLicenseNumberEqualsNotNull() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll()
                .forEach(e -> {
                    if (e.getLicense().getNumber() != null) {
                        map.put(e.getFirstName().concat(" " + e.getSecondName()), e.getLicense().getNumber());
                    }
                });
        LOG.info("Wyświetlono listę osób posiadających licencję z numerem");

        return map;
    }

    public Map<String, String> getMembersNamesWithLicenseNumberEqualsNotNullAndValidThruIsBefore() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> {
            if (e.getLicense().getNumber() != null && e.getLicense().getValidThru().isBefore(LocalDate.now())) {
                map.put(e.getFirstName().concat(" " + e.getSecondName() + " ważna do : " + e.getLicense().getValidThru()), e.getLicense().getNumber());
            }
        });
        LOG.info("Wyświetlono listę osób które nie mają przedłużonej licencji");
        return map;
    }

    public List<String> getMembersNamesWithoutLicense() {
        List<String> list = new ArrayList<>();
        memberRepository.findAll().forEach(e -> {
            if (e.getLicense().getNumber() == null) {
                list.add(e.getFirstName().concat(" " + e.getSecondName()));
            }
        });
        LOG.info("Lista osób które nie posiadają licencji");
        return list;

    }

    public Map<String, String> getMembersAndTheirsContribution() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll()
                .forEach(e -> map.put(e.getFirstName().concat(" " + e.getSecondName())
                        , e.getContribution().getContribution().toString()));
        LOG.info("Wyświetlono klubowiczów i ważność składek");
        return map;
    }

    public Map<String, String> getMembersAndTheirsContributionIsValid() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> {
            if (e.getContribution().getContribution().isAfter(LocalDate.now())) {
                map.put(e.getFirstName().concat(" " + e.getSecondName()),
                        e.getContribution().getContribution().toString());
            }
        });
        LOG.info("Wyświetlono klubowiczów którzy mają opłacone składki");
        return map;
    }


    public Map<String, String> getMembersAndTheirsContributionIsNotValid() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> {
            if (e.getContribution().getContribution().isBefore(LocalDate.now())) {
                map.put((e.getFirstName().concat(" " + e.getSecondName())), e.getContribution().getContribution().toString());
            }
        });
        LOG.info("Wyświetlono klubowiczów którzy nie mają opłaconych składek");
        return map;
    }

    public Map<String, String> getMembersWhoHaveValidLicenseAndNotValidContribution() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> {
            if (e.getLicense()
                    .getValidThru()
                    .isAfter(LocalDate.now())
                    && e.getContribution()
                    .getContribution()
                    .isBefore(LocalDate.now())) {
                map.put(e.getFirstName().concat(" " + e.getSecondName()), e.getContribution().getContribution().toString());
            }
        });
        LOG.info("Osoby posiadające ważną licencję i nieważną składkę");
        return map;
    }

    //--------------------------------------------------------------------------
    public UUID addNewMember(Member member) throws Exception {
        MemberEntity memberEntity = null;
        if (member.getEmail() == null) {
            member.setEmail("");
        }

        if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
            LOG.error("Ktoś już na taki numer PESEL");
        }
        if (member.getEmail() == null || member.getEmail().isEmpty()) {
            member.setEmail("");
        }
//        if (memberRepository.findByEmail(member.getEmail()).isPresent() && !member.getEmail().isEmpty()) {
//            LOG.error("Ktoś już ma taki adres e-mail");
//        }
        if (memberRepository.findByLegitimationNumber(member.getLegitimationNumber()).isPresent()) {
            LOG.error("Ktoś już ma taki numer legitymacji");
        }
        if (memberRepository.findByPhoneNumber(member.getPhoneNumber()).isPresent()) {
            LOG.error("Ktoś już ma taki numer telefonu");
        }
        if (memberRepository.findByIDCard(member.getIDCard()).isPresent()) {
            LOG.error("Ktoś już ma taki numer dowodu osobistego");
        } else {
            member.setIDCard(member.getIDCard().toUpperCase());
            if (member.getJoinDate() == null) {
                member.setJoinDate(LocalDate.now());
                LOG.info("ustawiono domyślną datę zapisu " + member.getJoinDate());
            }
            if (member.getLegitimationNumber() == null) {
                List<MemberEntity> numberList = new ArrayList<>(memberRepository.findAll());
                numberList.sort(Comparator.comparing(MemberEntity::getSecondName));
                Integer number = numberList.get(0).getLegitimationNumber() + 1;
                member.setLegitimationNumber(number);
                if (memberRepository.findByLegitimationNumber(member.getLegitimationNumber()).isPresent()) {
                    LOG.error("Ktoś już ma taki numer legitymacji");
                    System.out.println(numberList);
                    throw new Exception();
                }
                LOG.info("ustawiono domyślny numer legitymacji : " + member.getLegitimationNumber());
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
            LOG.info("Dodano nowego członka Klubu");
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
        }
        return Objects.requireNonNull(memberEntity).getUuid();


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
    public boolean activateOrDeactivateMember(UUID uuid) {
        if (memberRepository.existsById(uuid)) {
            MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
            LOG.info("Zmieniono status " + memberEntity.getFirstName());
            memberEntity.setActive(!memberEntity.getActive());
            memberRepository.saveAndFlush(memberEntity);
            return true;
        } else
            LOG.error("Nie znaleziono takiego klubowicza");
        return false;
    }


    //--------------------------------------------------------------------------
    public boolean updateMember(UUID uuid, Member member) {
        try {
            MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);

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
                    return false;
                } else {
                    memberEntity.setLegitimationNumber(member.getLegitimationNumber());
                    LOG.info(goodMessage() + "numer legitymacji");
                }
            }
            if (member.getEmail() != null && !member.getEmail().isEmpty()) {
                if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
                    LOG.error("Już ktoś ma taki sam e-mail");
                    return false;
                } else {
                    memberEntity.setEmail(member.getEmail().trim());
                    LOG.info(goodMessage() + "Email");
                }
            }
            if (member.getPesel() != null && !member.getPesel().isEmpty()) {
                if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
                    LOG.error("Już ktoś ma taki sam numer PESEL");
                    return false;
                } else {
                    memberEntity.setPesel(member.getPesel());
                    LOG.info(goodMessage() + "Numer PESEL");
                }
            }
            if (member.getPhoneNumber() != null && !member.getPhoneNumber().isEmpty()) {
                if (member.getPhoneNumber().replaceAll("\\s-", "").length() != 9 && !member.getPhoneNumber().isEmpty()) {
                    LOG.error("Żle podany numer");
                    return false;
                }
                String s = "+48";
                memberEntity.setPhoneNumber((s + member.getPhoneNumber()).replaceAll("\\s", ""));
                if (memberRepository.findByPhoneNumber((s + member.getPhoneNumber()).replaceAll("\\s", "")).isPresent()) {
                    LOG.error("Ktoś już ma taki numer telefonu");
                    return false;
                }
                if (member.getPhoneNumber().equals(memberEntity.getPhoneNumber())) {
                    memberEntity.setPhoneNumber(member.getPhoneNumber());
                    LOG.info(goodMessage() + "Numer Telefonu");
                }
            }
            if (member.getIDCard() != null && !member.getIDCard().isEmpty()) {
                if (memberRepository.findByIDCard(member.getIDCard()).isPresent()) {
                    LOG.error("Ktoś już ma taki numer dowodu");
                    return false;
                }
                memberEntity.setIDCard(member.getIDCard().toUpperCase());
                LOG.info(goodMessage() + " Numer Dowodu");
            }

            memberRepository.saveAndFlush(memberEntity);
            return true;
        } catch (
                EntityNotFoundException ex) {
            badMessage();
            return false;
        }
    }

    public boolean changeWeaponPermission(UUID memberUUID, WeaponPermission weaponPermission) {

        return weaponPermissionService.updateWeaponPermission(memberUUID, weaponPermission);
    }

    public boolean changeAdult(UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getAdult().equals(false)) {
            memberEntity.setAdult(true);
            LOG.info(" Klubowicz należy od teraz do grupy dorosłej : " + LocalDate.now());
        }
        memberRepository.saveAndFlush(memberEntity);
        return true;
    }


    private String goodMessage() {
        return "Zaktualizowano pomyślnie : ";
    }

    private void badMessage() {
        LOG.error("Nie znaleziono klubowicza");
    }

    public Optional<MemberEntity> getSingleMember(UUID uuid) {
        LOG.info("Wywołano membera");
        return memberRepository.findById(uuid);
    }

    public List<MemberEntity> getErasedMembers(Boolean erased) {
        LOG.info("Wyświetlono osoby skreślone z listy członków");
        return memberRepository.findAllByErased(erased);
    }

    public boolean eraseMember(UUID uuid) {
        MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getErased().equals(false)) {
            memberEntity.setErased(true);
            LOG.info("Skreślony");
        } else if (memberEntity.getErased().equals(true)) {
            memberEntity.setErased(false);
            LOG.info("Przywrócony");
        }

        memberRepository.saveAndFlush(memberEntity);
        return true;
    }
}
