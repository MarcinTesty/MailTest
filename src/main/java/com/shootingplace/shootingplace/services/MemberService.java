package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.*;
import com.shootingplace.shootingplace.repositories.MemberRepository;
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


    public MemberService(MemberRepository memberRepository,
                         AddressService addressService,
                         LicenseService licenseService,
                         ShootingPatentService shootingPatentService,
                         ContributionService contributionService
    ) {
        this.memberRepository = memberRepository;
        this.contributionService = contributionService;
        this.addressService = addressService;
        this.licenseService = licenseService;
        this.shootingPatentService = shootingPatentService;
    }


    //--------------------------------------------------------------------------
    public Map<UUID, Member> getMembers() {
        Map<UUID, Member> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> map.put(e.getUuid(), Mapping.map(e)));
        System.out.println("Wyświetlono listę członków klubu");
        System.out.println("Ilość klubowiczów aktywnych : " + memberRepository.findAllByActive(true).size());
        System.out.println("Ilość klubowiczów nieaktywnych : " + memberRepository.findAllByActive(false).size());
        System.out.println("liczba wpisów do rejestru : " + map.size());
        return map;
    }

    public Map<UUID, Member> getActiveMembers() {
        Map<UUID, Member> map = new HashMap<>();
        memberRepository.findAllByActive(true).forEach(e -> map.put(e.getUuid(), Mapping.map(e)));
        System.out.println("Ilość klubowiczów aktywnych : " + map.size());

        return map;
    }

    public Map<UUID, Member> getNonActiveMembers() {
        Map<UUID, Member> map = new HashMap<>();
        memberRepository.findAllByActive(false).forEach(e -> map.put(e.getUuid(), Mapping.map(e)));
        System.out.println("Ilość klubowiczów aktywnych : " + map.size());

        return map;
    }

    //    kurwa mać ponad tydzień główkowania dla takiej pierdoły
    public Map<String, String> getMembersNamesWithLicenseNumberEqualsNotNull() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll()
                .forEach(e -> {
                    if (e.getLicense().getNumber() != null) {
                        map.put(e.getFirstName().concat(" " + e.getSecondName()), e.getLicense().getNumber());
                    }
                });
        System.out.println("Wyświetlono listę osób posiadających licencję z numerem");

        return map;
    }

    public Map<String, String> getMembersNamesWithLicenseNumberEqualsNotNullAndValidThruIsBefore() {
        Map<String, String> map = new HashMap<>();
        memberRepository.findAll().forEach(e -> {
            if (e.getLicense().getNumber() != null && e.getLicense().getValidThru().isBefore(LocalDate.now())) {
                map.put(e.getFirstName().concat(" " + e.getSecondName() + " ważna do : " + e.getLicense().getValidThru()), e.getLicense().getNumber());
            }
        });
        System.out.println("Wyświetlono listę osób które nie mają przedłużonej licencji");
        return map;
    }

    public List<String> getMembersNamesWithoutLicense() {
        List<String> list = new ArrayList<>();
        memberRepository.findAll().forEach(e -> {
            if (e.getLicense().getNumber() == null) {
                list.add(e.getFirstName().concat(" " + e.getSecondName()));
            }
        });
        System.out.println("Lista osób które nie posiadają licencji");
        return list;

    }

    //--------------------------------------------------------------------------
    public UUID addMember(Member member) {
        MemberEntity memberEntity = null;
        if (memberRepository.findByPesel(member.getPesel()).isPresent()) {
            System.out.println("Ktoś już na taki numer PESEL");
        } else if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            System.out.println("Ktoś już ma taki adres e-mail");
        } else if (memberRepository.findByLegitimationNumber(member.getLegitimationNumber()).isPresent()) {
            System.out.println("Ktoś już ma taki numer legitymacji");
        } else if (memberRepository.findByPhoneNumber(member.getPhoneNumber()).isPresent()) {
            System.out.println("Ktoś już ma taki numer telefonu");
        } else {
            if (member.getJoinDate() == null) {
                System.out.println("ustawiono domyślną datę zapisu");
                member.setJoinDate(LocalDate.now());
            }
            if (member.getLegitimationNumber() == null) {
                System.out.println("ustawiono domyślny numer legitymacji");
                member.setLegitimationNumber(memberRepository.findAll().size() + 1);
            }
//            if (member.getActive().equals(false) || member.getActive() == null || member.getActive().equals(true)) {
//                System.out.println("Klubowicz nie jest jeszcze aktywny");
//                member.setActive(false);
//            }
            if (member.getWeaponPermission() == null) {
                System.out.println("Klubowicz nie posiada pozwolenia na broń");
                member.setWeaponPermission(false);
            }
            String s = "+48";
            if (member.getPhoneNumber() != null) {
                member.setPhoneNumber(s + member.getPhoneNumber().replaceAll("\\s", ""));
            }

            System.out.println("Dodano nowego członka Klubu");
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
                        .build();
                contributionService.addContribution(memberEntity.getUuid(), contribution);

            }
        }
        return Objects.requireNonNull(memberEntity).getUuid();
    }
//  I   Półrocze (mm-dd) od 01-01 do 06-30 max do 09-30
//  II  Półrocze (mm-dd) od 07-01 do 12-31 max do 03-31


    //--------------------------------------------------------------------------

    public boolean deleteMember(UUID uuid) {
        MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        if (memberRepository.existsById(uuid) && !memberEntity.getActive()) {
            memberRepository.deleteById(uuid);
            System.out.println("Usunięto członka klubu");
            return true;
        } else if (memberRepository.existsById(uuid) && memberEntity.getActive()) {
            System.out.println("Klubowicz jest aktywny");
            return false;
        } else
            System.out.println("Nie znaleziono takiego klubowicza");
        return false;
    }

    //--------------------------------------------------------------------------
    public boolean activateOrDeactivateMember(UUID uuid) {
        if (memberRepository.existsById(uuid)) {
            MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
            if (!memberEntity.getActive()) {
                System.out.println(memberEntity.getFirstName() + " jest już aktywny");
                memberEntity.setActive(true);
            } else {
                System.out.println(memberEntity.getFirstName() + " Jest nieaktywny");
                memberEntity.setActive(false);
            }
            memberRepository.saveAndFlush(memberEntity);
            return true;
        } else
            System.out.println("Nie znaleziono takiego klubowicza");
        return false;
    }


    //--------------------------------------------------------------------------
    public boolean updateMember(UUID uuid, Member member) {
        try {
            MemberEntity memberEntity = memberRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
            if (memberEntity.getActive().equals(false)) {
                System.out.println("Klubowicz nie aktywny");
                return false;
            } else {
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
                if (member.getPhoneNumber().replaceAll("\\s-", "").length() != 9) {
                    System.out.println("Żle podany numer");
                    return false;
                }
                if (member.getPhoneNumber() != null) {
                    String s = "+48";
                    memberEntity.setPhoneNumber((s + member.getPhoneNumber()).replaceAll("\\s", ""));
                    if (memberRepository.findByPhoneNumber((s + member.getPhoneNumber()).replaceAll("\\s", "")).isPresent()) {
                        System.out.println("Ktoś już ma taki numer telefonu");
                        return false;
                    }
                    System.out.println(goodMessage() + "Numer Telefonu");
                }
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
        System.out.println("Nie znaleziono klubowicza");
    }

}
