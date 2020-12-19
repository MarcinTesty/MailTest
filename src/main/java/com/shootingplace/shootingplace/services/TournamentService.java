package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.TournamentEntity;
import com.shootingplace.shootingplace.domain.enums.ArbiterWorkClass;
import com.shootingplace.shootingplace.domain.models.Tournament;
import com.shootingplace.shootingplace.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final MemberRepository memberRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;
    private final CompetitionRepository competitionRepository;
    private final HistoryService historyService;
    private final Logger LOG = LogManager.getLogger(getClass());


    public TournamentService(TournamentRepository tournamentRepository, MemberRepository memberRepository, CompetitionMembersListRepository competitionMembersListRepository, CompetitionRepository competitionRepository, HistoryService historyService) {
        this.tournamentRepository = tournamentRepository;
        this.memberRepository = memberRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
        this.competitionRepository = competitionRepository;
        this.historyService = historyService;
    }

    public UUID createNewTournament(Tournament tournament) {
        TournamentEntity tournamentEntity = Mapping.map(tournament);

        if (tournament.getMainArbiter() == null) {
            tournamentEntity.setMainArbiter(null);
        }
        if (tournament.getCommissionRTSArbiter() == null) {
            tournamentEntity.setCommissionRTSArbiter(null);
        }
        if (tournament.getDate() == null) {
            tournamentEntity.setDate(LocalDate.now());
        }

        tournamentRepository.saveAndFlush(tournamentEntity);
        LOG.info("Stworzono nowe zawody " + tournamentEntity.getName());

        return tournamentEntity.getUuid();
    }

    public boolean updateTournament(UUID tournamentUUID, Tournament tournament) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID)
                .orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            if (tournament.getName() != null) {
                if (!tournament.getName().isEmpty()) {
                    tournamentEntity.setName(tournament.getName());
                    LOG.info("Zmieniono nazwę zawodów");
                }
            }

            if (tournament.getDate() != null) {
                tournamentEntity.setDate(tournament.getDate());
                LOG.info("Zmieniono datę zawodów");
            }
            tournamentRepository.saveAndFlush(tournamentEntity);
            historyService.updateTournamentEntityInCompetitionHistory(tournamentUUID);
            historyService.updateTournamentInJudgingHistory(tournamentUUID);
            return true;
        }

        LOG.warn("Zawody są już zamknięte i nie można już nic zrobić");
        return true;
    }


    public List<TournamentEntity> getListOfTournaments() {
        LOG.info("Wyświetlono listę zawodów");
        List<TournamentEntity> list = new ArrayList<>(tournamentRepository.findAll());
        list.sort(Comparator.comparing(TournamentEntity::getDate).reversed());
        return list;
    }

    public boolean closeTournament(UUID tournamentUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            LOG.info("Zawody " + tournamentEntity.getName() + " zostały zamknięte");
            tournamentEntity.setOpen(false);
            tournamentRepository.saveAndFlush(tournamentEntity);
            return true;
        }
        return false;
    }

    public boolean removeArbiterFromTournament(UUID tournamentUUID, UUID memberUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            String function = ArbiterWorkClass.HELP.getName();

            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);


            Set<MemberEntity> set = tournamentEntity.getArbitersList();

            set.remove(memberEntity);

            tournamentRepository.saveAndFlush(tournamentEntity);

            historyService.removeJudgingRecord(memberUUID, tournamentUUID, function);
            return true;
        }
        return false;
    }

    public boolean addMainArbiter(UUID tournamentUUID, UUID memberUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            String function = ArbiterWorkClass.MAIN_ARBITER.getName();

            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            if (!memberEntity.getMemberPermissions().getArbiterNumber().isEmpty()) {
                if (tournamentEntity.getMainArbiter() == null || tournamentEntity.getMainArbiter() != memberEntity) {
                    if (tournamentEntity.getMainArbiter() == null) {
                        tournamentEntity.setMainArbiter(memberEntity);

                    } else {
                        historyService.removeJudgingRecord(tournamentEntity.getMainArbiter().getUuid(), tournamentUUID, function);
                        tournamentEntity.setMainArbiter(memberEntity);
                    }
                    tournamentRepository.saveAndFlush(tournamentEntity);
                    LOG.info("Ustawiono sędziego głównego zawodów");
                    historyService.addJudgingRecord(memberUUID, tournamentUUID, function);
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean addRTSArbiter(UUID tournamentUUID, UUID memberUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            String function = ArbiterWorkClass.RTS_ARBITER.getName();

            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            if (!memberEntity.getMemberPermissions().getArbiterNumber().isEmpty()) {
                if (tournamentEntity.getCommissionRTSArbiter() == null || tournamentEntity.getCommissionRTSArbiter() != memberEntity) {
                    if (tournamentEntity.getCommissionRTSArbiter() == null) {
                        tournamentEntity.setCommissionRTSArbiter(memberEntity);

                    } else {
                        historyService.removeJudgingRecord(tournamentEntity.getCommissionRTSArbiter().getUuid(), tournamentUUID, function);
                        tournamentEntity.setCommissionRTSArbiter(memberEntity);
                    }
                    tournamentRepository.saveAndFlush(tournamentEntity);
                    LOG.info("Ustawiono sędziego biura obliczeń");
                    historyService.addJudgingRecord(memberUUID, tournamentUUID, function);
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean addOthersArbiters(UUID tournamentUUID, UUID memberUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            String function = ArbiterWorkClass.HELP.getName();

            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            if (!memberEntity.getMemberPermissions().getArbiterNumber().isEmpty()) {
                Set<MemberEntity> set = tournamentEntity.getArbitersList();
                set.add(memberEntity);


                tournamentEntity.setArbitersList(set);
                tournamentRepository.saveAndFlush(tournamentEntity);
                LOG.info("Dodano sędziego pomocniczego");
                historyService.addJudgingRecord(memberUUID, tournamentUUID, function);
            }
            return true;
        }
        return false;
    }

    public boolean addNewCompetitionListToTournament(UUID tournamentUUID, UUID competitionUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            if (competitionUUID != null) {
                CompetitionEntity competition = competitionRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
                if (!tournamentEntity.getCompetitionsList().isEmpty()) {
                    for (int i = 0; i < tournamentEntity.getCompetitionsList().size(); i++) {
                        if (tournamentEntity.getCompetitionsList().get(i).getName().equals(competition.getName())) {
                            LOG.info("Nie można dodać konkurencji bo taka już istnieje w zawodach");
                            return false;
                        }
                    }
                }
                CompetitionMembersListEntity competitionMembersList = CompetitionMembersListEntity.builder()
                        .name(competition.getName())
                        .attachedToTournament(tournamentEntity.getUuid())
                        .date(tournamentEntity.getDate())
                        .build();
                competitionMembersListRepository.saveAndFlush(competitionMembersList);
                List<CompetitionMembersListEntity> competitionsList = tournamentEntity.getCompetitionsList();
                competitionsList.add(competitionMembersList);
                tournamentRepository.saveAndFlush(tournamentEntity);
                LOG.info("Dodano konkurencję do zawodów");

            }
            return true;
        }
        return false;
    }
}
