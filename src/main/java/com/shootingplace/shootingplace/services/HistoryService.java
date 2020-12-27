package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.models.History;
import com.shootingplace.shootingplace.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final LicenseRepository licenseRepository;
    private final CompetitionHistoryRepository competitionHistoryRepository;
    private final TournamentRepository tournamentRepository;
    private final JudgingHistoryRepository judgingHistoryRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public HistoryService(HistoryRepository historyRepository, MemberRepository memberRepository, LicenseRepository licenseRepository, CompetitionHistoryRepository competitionHistoryRepository, TournamentRepository tournamentRepository, JudgingHistoryRepository judgingHistoryRepository) {
        this.historyRepository = historyRepository;
        this.memberRepository = memberRepository;
        this.licenseRepository = licenseRepository;
        this.competitionHistoryRepository = competitionHistoryRepository;
        this.tournamentRepository = tournamentRepository;
        this.judgingHistoryRepository = judgingHistoryRepository;
    }

    //  Basic
    void createHistory(UUID memberUUID, History history) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new);
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
        historyRepository.saveAndFlush(historyEntity);
        memberEntity.setHistory(historyEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Historia została utworzona");
    }

    // Contribution
    void addContribution(UUID memberUUID, ContributionEntity contribution) {
        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();

        historyEntity
                .getContributionList()
                .add(contribution);

        historyEntity
                .getContributionList()
                .sort(Comparator.comparing(ContributionEntity::getPaymentDay)
                        .thenComparing(ContributionEntity::getValidThru).reversed());

        LOG.info("Dodano rekord w historii składek");
        historyRepository.saveAndFlush(historyEntity);
    }

    void removeContribution(UUID memberUUID, ContributionEntity contribution) {
        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        historyEntity
                .getContributionList()
                .remove(contribution);
        historyRepository.saveAndFlush(historyEntity);
    }

    // license
    void addLicenseHistoryRecord(UUID memberUUID, int index) {
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

    void addDateToPatentPermissions(UUID memberUUID, LocalDate date, int index) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate[] dateTab = historyEntity.getPatentDay().clone();
        if (index == 0) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[0] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano datę patentu dla Pistoletu");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[0] == null) {
                    dateTab[0] = date;
                    LOG.info("Ustawiono datę patentu Karabinu na domyślną");
                }
            }
        }
        if (index == 1) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[1] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano datę patentu dla Karabinu");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[1] == null) {
                    dateTab[1] = date;
                    LOG.info("Ustawiono datę patentu Karabinu na domyślną");
                }
            }
        }
        if (index == 2) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[2] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano datę patentu dla Strzelby");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[2] == null) {
                    dateTab[2] = date;
                    LOG.info("Ustawiono datę patentu Strzelby na domyślną");
                }
            }
        }
        if (!historyEntity.getPatentFirstRecord()) {
            LOG.info("Już wpisano datę pierwszego nadania patentu");
        }
        historyEntity.setPatentDay(dateTab);
        historyRepository.saveAndFlush(historyEntity);

    }

    public Boolean addLicenseHistoryPayment(UUID memberUUID) {
        LicenseEntity licenseEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getLicense();

        try {
            HistoryEntity historyEntity = memberRepository.findById(memberUUID)
                    .orElseThrow(EntityNotFoundException::new)
                    .getHistory();
            if (!licenseEntity.getPaid()) {
                if (historyEntity.getLicensePaymentHistory() != null) {
                    LocalDate[] newState = new LocalDate[historyEntity.getLicensePaymentHistory().length + 1];

                    for (int i = 0; i <= historyEntity.getLicensePaymentHistory().length - 1; i++) {
                        newState[i] = historyEntity.getLicensePaymentHistory()[i];
                        newState[i + 1] = LocalDate.now();
                    }
                    LocalDate[] sortState = selectionSort(newState);
                    historyEntity.setLicensePaymentHistory(sortState);
                    LOG.info("Dodano wpis o nowej płatności za licencję " + LocalDate.now());
                    historyRepository.saveAndFlush(historyEntity);

                } else {

                    LocalDate[] newState = new LocalDate[1];
                    newState[0] = LocalDate.now();
                    historyEntity.setLicensePaymentHistory(newState);
                    LOG.info("Dodano wpis o nowej płatności za licencję " + LocalDate.now());
                    historyRepository.saveAndFlush(historyEntity);
                }

            } else {
                return false;
            }
        } catch (EntityNotFoundException e) {
            return false;
        }

        licenseEntity.setPaid(true);
        licenseRepository.saveAndFlush(licenseEntity);
        return true;
    }


    private LocalDate[] selectionSort(LocalDate[] array) {

        int n = array.length;

        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j].isAfter(array[min])) {
                    min = j;
                }
            }
            LocalDate temp = array[min];
            array[min] = array[i];
            array[i] = temp;
        }
        return array;
    }

    private void sort(String[] array) {

        Arrays.sort(array);
    }

    private void reverse(List array) {
        Collections.reverse(array);
    }

    //  Tournament
    private CompetitionHistoryEntity createCompetitionHistoryEntity(UUID tournamentUUID, LocalDate date, String discipline, UUID attachedTo) {
        String name = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new).getName();
        return CompetitionHistoryEntity.builder()
                .name(name)
                .date(date)
                .discipline(discipline)
                .attachedToList(attachedTo)
                .build();

    }

    void addCompetitionRecord(UUID memberUUID, CompetitionMembersListEntity list) {

        CompetitionHistoryEntity competitionHistoryEntity = createCompetitionHistoryEntity(list.getAttachedToTournament(), list.getDate(), list.getName(), list.getUuid());
        competitionHistoryRepository.saveAndFlush(competitionHistoryEntity);

        List<CompetitionHistoryEntity> competitionHistoryEntityList = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory()
                .getCompetitionHistory();

        competitionHistoryEntityList.add(competitionHistoryEntity);

        HistoryEntity historyEntity = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        competitionHistoryEntityList.sort(Comparator.comparing(CompetitionHistoryEntity::getDate).reversed());
        historyEntity.setCompetitionHistory(competitionHistoryEntityList);

        if (list.getName().toUpperCase().startsWith("P")) {
            Integer pistolCounter = historyEntity.getPistolCounter() + 1;
            historyEntity.setPistolCounter(pistolCounter);
        }
        if (list.getName().toUpperCase().startsWith("K")) {
            Integer rifleCounter = historyEntity.getRifleCounter() + 1;
            historyEntity.setRifleCounter(rifleCounter);
        }
        if (list.getName().toUpperCase().startsWith("S")) {
            Integer shotgunCounter = historyEntity.getShotgunCounter() + 1;
            historyEntity.setShotgunCounter(shotgunCounter);
        }

        LOG.info("Dodano wpis w historii startów.");
        historyRepository.saveAndFlush(historyEntity);

        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        if (historyEntity.getPistolCounter() >= 4 || historyEntity.getRifleCounter() >= 4 || historyEntity.getShotgunCounter() >= 4) {
            if (historyEntity.getPistolCounter() >= 4 && (historyEntity.getRifleCounter() >= 2 || historyEntity.getShotgunCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                System.out.println("może przedłużyć licencję");
                licenseRepository.saveAndFlush(memberEntity.getLicense());
            }
            if (historyEntity.getRifleCounter() >= 4 && (historyEntity.getPistolCounter() >= 2 || historyEntity.getShotgunCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());
                System.out.println("może przedłużyć licencję");

            }
            if (historyEntity.getShotgunCounter() >= 4 && (historyEntity.getRifleCounter() >= 2 || historyEntity.getPistolCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());
                System.out.println("może przedłużyć licencję");
            }
        }
    }

    void removeCompetitionRecord(UUID memberUUID, CompetitionMembersListEntity list) {
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
        if (list.getName().toUpperCase().startsWith("P")) {
            Integer pistolCounter = historyEntity.getPistolCounter() - 1;
            historyEntity.setPistolCounter(pistolCounter);
        }
        if (list.getName().toUpperCase().startsWith("K")) {
            Integer rifleCounter = historyEntity.getRifleCounter() - 1;
            historyEntity.setRifleCounter(rifleCounter);
        }
        if (list.getName().toUpperCase().startsWith("S")) {
            Integer shotgunCounter = historyEntity.getShotgunCounter() - 1;
            historyEntity.setShotgunCounter(shotgunCounter);
        }


        LOG.info("Zaktualizowano wpis w historii startów");
        historyRepository.saveAndFlush(historyEntity);
    }

    void addJudgingRecord(UUID memberUUID, UUID tournamentUUID, String function) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);

        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();

        JudgingHistoryEntity judgingHistoryEntity = createJudgingHistoryEntity(tournamentEntity.getDate(), tournamentEntity.getName(), tournamentEntity.getUuid(), function);

        List<JudgingHistoryEntity> judgingHistory = historyEntity.getJudgingHistory();

        judgingHistory.add(judgingHistoryEntity);
        judgingHistory.sort(Comparator.comparing(JudgingHistoryEntity::getDate).reversed());
        judgingHistoryRepository.saveAndFlush(judgingHistoryEntity);
        historyEntity.setJudgingHistory(judgingHistory);

        historyRepository.saveAndFlush(historyEntity);
    }

    void removeJudgingRecord(UUID memberUUID, UUID tournamentUUID, String function) {

        List<JudgingHistoryEntity> judgingHistoryEntityList = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory().getJudgingHistory();

        JudgingHistoryEntity any = judgingHistoryEntityList
                .stream()
                .filter(e -> e.getTournamentUUID().equals(tournamentUUID) && e.getFunction().equals(function))
                .findAny().orElseThrow(EntityNotFoundException::new);
        judgingHistoryEntityList.remove(any);
        HistoryEntity historyEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory();
        historyEntity.setJudgingHistory(judgingHistoryEntityList);
        historyRepository.saveAndFlush(historyEntity);

    }

    private JudgingHistoryEntity createJudgingHistoryEntity(LocalDate date, String name, UUID tournamentUUID, String function) {
        return JudgingHistoryEntity.builder()
                .date(date)
                .name(name)
                .function(function)
                .tournamentUUID(tournamentUUID)
                .build();
    }

    void updateTournamentEntityInCompetitionHistory(UUID tournamentUUID) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        tournamentEntity.getCompetitionsList().forEach(competitionList -> competitionList
                .getMembersList()
                .forEach(member -> member
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

    void updateTournamentInJudgingHistory(UUID tournamentUUID) {
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
