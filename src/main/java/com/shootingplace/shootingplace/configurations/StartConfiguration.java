package com.shootingplace.shootingplace.configurations;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import com.shootingplace.shootingplace.repositories.ClubRepository;
import com.shootingplace.shootingplace.repositories.HistoryRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.PersonalEvidenceRepository;
import com.shootingplace.shootingplace.services.HistoryService;
import com.shootingplace.shootingplace.services.PersonalEvidenceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartConfiguration {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final PersonalEvidenceService personalEvidenceService;
    private final PersonalEvidenceRepository personalEvidenceRepository;
    private final HistoryService historyService;
    private final HistoryRepository historyRepository;

    public StartConfiguration(ClubRepository clubRepository, MemberRepository memberRepository, PersonalEvidenceService personalEvidenceService, PersonalEvidenceRepository personalEvidenceRepository, HistoryService historyService, HistoryRepository historyRepository) {
        this.clubRepository = clubRepository;
        this.memberRepository = memberRepository;
        this.personalEvidenceService = personalEvidenceService;
        this.personalEvidenceRepository = personalEvidenceRepository;
        this.historyService = historyService;
        this.historyRepository = historyRepository;
    }

    @Bean
    public CommandLineRunner initClub() {
        return args ->
                clubRepository.saveAndFlush(ClubEntity.builder()
                        .id(1)
                        .name("DZIESIĄTKA LOK ŁÓDŹ")
                        .build());
    }
//
//    @Bean
//    public CommandLineRunner initZeroMember() {
//        PersonalEvidenceEntity personalEvidenceEntity = PersonalEvidenceEntity.builder()
//                .file(null)
//                .build();
//        personalEvidenceRepository.saveAndFlush(personalEvidenceEntity);
//        HistoryEntity historyEntity = HistoryEntity.builder()
//                .contributionList(new ArrayList<>())
//                .licenseHistory(new String[3])
//                .patentDay(new LocalDate[3])
//                .pistolCounter(0)
//                .rifleCounter(0)
//                .shotgunCounter(0)
//                .competitionHistory(new ArrayList<>())
//                .judgingHistory(new ArrayList<>())
//                .patentFirstRecord(false)
//                .build();
//        historyRepository.saveAndFlush(historyEntity);
//        return args ->
//                memberRepository.saveAndFlush(MemberEntity.builder()
//                        .firstName("Zero")
//                        .secondName("Zero")
//                        .legitimationNumber(0)
//                        .address(null)
//                        .erased(false)
//                        .adult(true)
//                        .IDCard("OOO000000")
//                        .license(null)
//                        .phoneNumber("+48000000000")
//                        .active(true)
//                        .pesel("00010113653")
//                        .memberPermissions(null)
//                        .erasedReason(null)
//                        .shootingPatent(null)
//                        .email("000000@00.pl")
//                        .weaponPermission(null)
//                        .joinDate(LocalDate.now())
//                        .club(clubRepository.findById(1).orElseThrow())
//                        .personalEvidence(personalEvidenceEntity)
//                        .history(historyEntity)
//                        .build());
//
//    }

}
