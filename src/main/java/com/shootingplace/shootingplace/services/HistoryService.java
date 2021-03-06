package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.enums.Discipline;
import com.shootingplace.shootingplace.domain.models.History;
import com.shootingplace.shootingplace.repositories.*;
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
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final LicenseRepository licenseRepository;
    private final CompetitionHistoryRepository competitionHistoryRepository;
    private final TournamentRepository tournamentRepository;
    private final JudgingHistoryRepository judgingHistoryRepository;
    private final ContributionRepository contributionRepository;
    private final LicensePaymentHistoryRepository licensePaymentHistoryRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public HistoryService(HistoryRepository historyRepository, MemberRepository memberRepository, LicenseRepository licenseRepository, CompetitionHistoryRepository competitionHistoryRepository, TournamentRepository tournamentRepository, JudgingHistoryRepository judgingHistoryRepository, ContributionRepository contributionRepository, LicensePaymentHistoryRepository licensePaymentHistoryRepository) {
        this.historyRepository = historyRepository;
        this.memberRepository = memberRepository;
        this.licenseRepository = licenseRepository;
        this.competitionHistoryRepository = competitionHistoryRepository;
        this.tournamentRepository = tournamentRepository;
        this.judgingHistoryRepository = judgingHistoryRepository;
        this.contributionRepository = contributionRepository;
        this.licensePaymentHistoryRepository = licensePaymentHistoryRepository;
    }

    //  Basic
    HistoryEntity createHistory(History history) {
        HistoryEntity historyEntity = Mapping.map(history);
        historyEntity.setContributionsList(new ArrayList<>());
        historyEntity.setLicenseHistory(new String[3]);
        historyEntity.setPatentDay(new LocalDate[3]);
        historyEntity.setPistolCounter(0);
        historyEntity.setRifleCounter(0);
        historyEntity.setShotgunCounter(0);
        historyEntity.setCompetitionHistory(new ArrayList<>());
        historyEntity.setJudgingHistory(new ArrayList<>());
        historyEntity.setPatentFirstRecord(false);
        LOG.info("Historia zosta??a utworzona");
        return historyRepository.saveAndFlush(historyEntity);
    }

    // Contribution
    void addContribution(String memberUUID, ContributionEntity contribution) {
        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        contribution.setHistoryUUID(historyEntity.getUuid());
        List<ContributionEntity> contributionList = historyEntity
                .getContributionList();
        contributionList
                .sort(Comparator.comparing(ContributionEntity::getPaymentDay));
        contributionList.add(contribution);
        contributionList
                .sort(Comparator.comparing(ContributionEntity::getPaymentDay).reversed());
        historyEntity.setContributionsList(contributionList);

        LOG.info("Dodano rekord w historii sk??adek");
        historyRepository.saveAndFlush(historyEntity);

    }

    void removeContribution(String memberUUID, ContributionEntity contribution) {
        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        historyEntity
                .getContributionList()
                .remove(contribution);
        contribution.setHistoryUUID(null);
        contributionRepository.saveAndFlush(contribution);
        LOG.info("Usuni??to sk??adk??");
        historyRepository.saveAndFlush(historyEntity);
    }

    // license
    void addLicenseHistoryRecord(String memberUUID, int index) {
        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();

        String[] licenseTab = historyEntity.getLicenseHistory().clone();
        if (index == 0) {
            licenseTab[0] = "Pistolet";
        }
        if (index == 1) {
            licenseTab[1] = "Karabin";
        }
        if (index == 2) {
            licenseTab[2] = "Strzelba";
        }
        historyEntity.setLicenseHistory(licenseTab);
        historyRepository.saveAndFlush(historyEntity);
    }

    void addDateToPatentPermissions(String memberUUID, LocalDate date, int index) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate[] dateTab = historyEntity.getPatentDay().clone();
        if (index == 0) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[0] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano dat?? patentu dla Pistoletu");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[0] == null) {
                    dateTab[0] = date;
                    LOG.info("Ustawiono dat?? patentu Karabinu na domy??ln??");
                }
            }
        }
        if (index == 1) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[1] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano dat?? patentu dla Karabinu");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[1] == null) {
                    dateTab[1] = date;
                    LOG.info("Ustawiono dat?? patentu Karabinu na domy??ln??");
                }
            }
        }
        if (index == 2) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[2] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano dat?? patentu dla Strzelby");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[2] == null) {
                    dateTab[2] = date;
                    LOG.info("Ustawiono dat?? patentu Strzelby na domy??ln??");
                }
            }
        }
        if (!historyEntity.getPatentFirstRecord()) {
            LOG.info("Ju?? wpisano dat?? pierwszego nadania patentu");
        }
        historyEntity.setPatentDay(dateTab);
        historyRepository.saveAndFlush(historyEntity);

    }

    public Boolean addLicenseHistoryPayment(String memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LicenseEntity licenseEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getLicense();

        HistoryEntity historyEntity = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        if (memberEntity.getActive() && !licenseEntity.isPaid()) {
            if (historyEntity.getLicensePaymentHistory() != null) {
                int dateYear;
                if (memberEntity.getLicense().getValidThru() != null) {
                    dateYear = memberEntity.getLicense().getValidThru().getYear() + 1;
                } else {
                    dateYear = LocalDate.now().getYear();
                }
                List<LicensePaymentHistoryEntity> licensePaymentHistory = historyEntity.getLicensePaymentHistory();
                LicensePaymentHistoryEntity build = LicensePaymentHistoryEntity.builder()
                        .date(LocalDate.now())
                        .validForYear(dateYear)
                        .memberUUID(memberUUID)
                        .build();
                licensePaymentHistoryRepository.saveAndFlush(build);
                licensePaymentHistory.add(build);

            } else {
                LicensePaymentHistoryEntity build = LicensePaymentHistoryEntity.builder()
                        .date(LocalDate.now())
                        .validForYear(memberEntity.getLicense().getValidThru().getYear() + 1)
                        .memberUUID(memberUUID)
                        .build();
                licensePaymentHistoryRepository.saveAndFlush(build);
                List<LicensePaymentHistoryEntity> list = new ArrayList<>();
                list.add(build);
                historyEntity.setLicensePaymentHistory(list);
            }
            LOG.info("Dodano wpis o nowej p??atno??ci za licencj?? " + LocalDate.now());
            historyRepository.saveAndFlush(historyEntity);

        } else {
            return false;
        }

        licenseEntity.setPaid(true);
        licenseRepository.saveAndFlush(licenseEntity);
        return true;

    }

    //  Tournament
    private CompetitionHistoryEntity createCompetitionHistoryEntity(String tournamentUUID, LocalDate date, String discipline, String attachedTo) {
        String name = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new).getName();
        return CompetitionHistoryEntity.builder()
                .name(name)
                .date(date)
                .discipline(discipline)
                .attachedToList(attachedTo)
                .build();

    }

    void addCompetitionRecord(String memberUUID, CompetitionMembersListEntity list) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        CompetitionHistoryEntity competitionHistoryEntity = createCompetitionHistoryEntity(list.getAttachedToTournament(), list.getDate(), list.getDiscipline(), list.getUuid());
        competitionHistoryRepository.saveAndFlush(competitionHistoryEntity);

        List<CompetitionHistoryEntity> competitionHistoryEntityList = memberEntity
                .getHistory()
                .getCompetitionHistory();


        competitionHistoryEntityList.add(competitionHistoryEntity);
        competitionHistoryEntityList.sort(Comparator.comparing(CompetitionHistoryEntity::getDate).reversed());

        HistoryEntity historyEntity = memberEntity
                .getHistory();
        historyEntity.setCompetitionHistory(competitionHistoryEntityList);

        int licenseYear;
        if (memberEntity.getLicense().getValidThru() != null) {
            licenseYear = memberEntity.getLicense().getValidThru().getYear();
        } else {
            licenseYear = LocalDate.now().getYear();
        }
        List<CompetitionHistoryEntity> collectPistol = historyEntity.getCompetitionHistory()
                .stream()
                .filter(f -> f.getDiscipline().equals(Discipline.PISTOL.getName()))
                .filter(f -> f.getDate().getYear() == licenseYear)
                .collect(Collectors.toList());

        List<CompetitionHistoryEntity> collectRifle = historyEntity.getCompetitionHistory()
                .stream()
                .filter(f -> f.getDiscipline().equals(Discipline.RIFLE.getName()))
                .filter(f -> f.getDate().getYear() == licenseYear)
                .collect(Collectors.toList());


        List<CompetitionHistoryEntity> collectShotgun = historyEntity.getCompetitionHistory()
                .stream()
                .filter(f -> f.getDiscipline().equals(Discipline.SHOTGUN.getName()))
                .filter(f -> f.getDate().getYear() == licenseYear)
                .collect(Collectors.toList());
        historyEntity.setPistolCounter(collectPistol.size());
        historyEntity.setRifleCounter(collectRifle.size());
        historyEntity.setShotgunCounter(collectShotgun.size());

        LOG.info("Dodano wpis w historii start??w.");
        historyRepository.saveAndFlush(historyEntity);


        if (historyEntity.getPistolCounter() >= 4 || historyEntity.getRifleCounter() >= 4 || historyEntity.getShotgunCounter() >= 4) {
            if (historyEntity.getPistolCounter() >= 4 && (historyEntity.getRifleCounter() >= 2 || historyEntity.getShotgunCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());
            }
            if (historyEntity.getRifleCounter() >= 4 && (historyEntity.getPistolCounter() >= 2 || historyEntity.getShotgunCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());

            }
            if (historyEntity.getShotgunCounter() >= 4 && (historyEntity.getRifleCounter() >= 2 || historyEntity.getPistolCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());
            }
        }
    }

    void removeCompetitionRecord(String memberUUID, CompetitionMembersListEntity list) {
        HistoryEntity historyEntity = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        CompetitionHistoryEntity competitionHistoryEntity = new CompetitionHistoryEntity();
        for (CompetitionHistoryEntity e : historyEntity.getCompetitionHistory()) {
            if (e.getAttachedToList().equals(list.getUuid())) {
                competitionHistoryEntity = competitionHistoryRepository.findById(e.getUuid()).orElseThrow(EntityNotFoundException::new);
                break;
            }

        }
        historyEntity.getCompetitionHistory().remove(competitionHistoryEntity);
        historyRepository.saveAndFlush(historyEntity);
        if (list.getDiscipline().equals(Discipline.PISTOL.getName())) {
            Integer pistolCounter = historyEntity.getPistolCounter() - 1;
            historyEntity.setPistolCounter(pistolCounter);
        }
        if (list.getDiscipline().equals(Discipline.RIFLE.getName())) {
            Integer rifleCounter = historyEntity.getRifleCounter() - 1;
            historyEntity.setRifleCounter(rifleCounter);
        }
        if (list.getDiscipline().equals(Discipline.SHOTGUN.getName())) {
            Integer shotgunCounter = historyEntity.getShotgunCounter() - 1;
            historyEntity.setShotgunCounter(shotgunCounter);
        }


        LOG.info("Zaktualizowano wpis w historii start??w");
        historyRepository.saveAndFlush(historyEntity);
    }

    void addJudgingRecord(String memberUUID, String tournamentUUID, String function) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);

        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();

        JudgingHistoryEntity judgingHistoryEntity = createJudgingHistoryEntity(tournamentEntity.getDate(), tournamentEntity.getName(), tournamentEntity.getUuid(), function);

        List<JudgingHistoryEntity> judgingHistory = historyEntity.getJudgingHistory();

        judgingHistory.add(judgingHistoryEntity);
        judgingHistoryRepository.saveAndFlush(judgingHistoryEntity);
        historyEntity.setJudgingHistory(judgingHistory);

        historyRepository.saveAndFlush(historyEntity);
    }

    void removeJudgingRecord(String memberUUID, String tournamentUUID, String function) {

        List<JudgingHistoryEntity> judgingHistoryEntityList = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory().getJudgingHistory();

        JudgingHistoryEntity any = judgingHistoryEntityList
                .stream()
                .filter(e -> e.getTournamentUUID().equals(tournamentUUID))
                .filter(e -> e.getJudgingFunction().equals(function))
                .findFirst().orElseThrow(EntityNotFoundException::new);
        judgingHistoryEntityList.remove(any);
        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        historyEntity.setJudgingHistory(judgingHistoryEntityList);
        historyRepository.saveAndFlush(historyEntity);

    }

    private JudgingHistoryEntity createJudgingHistoryEntity(LocalDate date, String name, String tournamentUUID, String function) {
        return JudgingHistoryEntity.builder()
                .date(date)
                .name(name)
                .judgingFunction(function)
                .tournamentUUID(tournamentUUID)
                .build();
    }

    void updateTournamentEntityInCompetitionHistory(String tournamentUUID) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        tournamentEntity.getCompetitionsList().forEach(competitionList -> competitionList
                .getScoreList()
                .forEach(scoreEntity -> scoreEntity.getMember()
                        .getHistory()
                        .getCompetitionHistory()
                        .stream()
                        .filter(f -> f.getAttachedToList().equals(competitionList.getUuid()))
                        .forEach(f -> {
                            f.setName(tournamentEntity.getName());
                            f.setDate(tournamentEntity.getDate());
                            competitionHistoryRepository.saveAndFlush(f);
                        })));
    }

    void updateTournamentInJudgingHistory(String tournamentUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getArbitersList() != null) {
            tournamentEntity
                    .getArbitersList()
                    .forEach(member -> member
                            .getHistory()
                            .getJudgingHistory()
                            .stream()
                            .filter(f -> f.getTournamentUUID().equals(tournamentUUID))
                            .forEach(f -> {
                                f.setName(tournamentEntity.getName());
                                f.setDate(tournamentEntity.getDate());
                                judgingHistoryRepository.saveAndFlush(f);
                            })
                    );
        }
        if (tournamentEntity.getMainArbiter() != null) {
            tournamentEntity.getMainArbiter()
                    .getHistory()
                    .getJudgingHistory()
                    .stream()
                    .filter(f -> f.getTournamentUUID().equals(tournamentUUID))
                    .forEach(f -> {
                        f.setName(tournamentEntity.getName());
                        f.setDate(tournamentEntity.getDate());
                        judgingHistoryRepository.saveAndFlush(f);
                    });
        }
        if (tournamentEntity.getCommissionRTSArbiter() != null) {
            tournamentEntity.getCommissionRTSArbiter()
                    .getHistory()
                    .getJudgingHistory()
                    .stream()
                    .filter(f -> f.getTournamentUUID().equals(tournamentUUID))
                    .forEach(f -> {
                        f.setName(tournamentEntity.getName());
                        f.setDate(tournamentEntity.getDate());
                        judgingHistoryRepository.saveAndFlush(f);
                    });
        }

    }


}
