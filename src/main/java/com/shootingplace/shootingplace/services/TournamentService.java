package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.TournamentEntity;
import com.shootingplace.shootingplace.domain.models.Tournament;
import com.shootingplace.shootingplace.repositories.CompetitionMembersListRepository;
import com.shootingplace.shootingplace.repositories.CompetitionRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.TournamentRepository;
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
    private final Logger LOG = LogManager.getLogger(getClass());


    public TournamentService(TournamentRepository tournamentRepository, MemberRepository memberRepository, CompetitionMembersListRepository competitionMembersListRepository, CompetitionRepository competitionRepository) {
        this.tournamentRepository = tournamentRepository;
        this.memberRepository = memberRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
        this.competitionRepository = competitionRepository;
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
        LOG.info("Stworzono nowe zawody z identyfikatorem : " + tournamentEntity.getUuid());

        return tournamentEntity.getUuid();
    }

    public Boolean updateTournament(UUID tournamentUUID, Tournament tournament) {
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
            if (tournament.getMainArbiter() != null) {
                MemberEntity memberEntity = memberRepository.findByLegitimationNumber(tournament.getMainArbiter().getLegitimationNumber()).orElseThrow(EntityNotFoundException::new);
                tournamentEntity.setMainArbiter(memberEntity);
                LOG.info("Ustawiono sędziego głównego");
            }
            if (tournament.getCommissionRTSArbiter() != null) {
                MemberEntity memberEntity = memberRepository.findByLegitimationNumber(tournament.getCommissionRTSArbiter().getLegitimationNumber()).orElseThrow(EntityNotFoundException::new);
                tournamentEntity.setCommissionRTSArbiter(memberEntity);
                LOG.info("Ustawiono sędziego RTS");
            }
            tournamentRepository.saveAndFlush(tournamentEntity);
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

    public Boolean closeTournament(UUID tournamentUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.getOpen()) {
            LOG.info("Zawody " + tournamentEntity.getName() + " zostały zamknięte");
            tournamentEntity.setOpen(false);
            tournamentRepository.saveAndFlush(tournamentEntity);
            return true;
        }
        return false;
    }

    public void addMainArbiter(UUID tournamentUUID, UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (!memberEntity.getMemberPermissions().getArbiterNumber().isEmpty()) {
            Tournament tournament = Tournament.builder().mainArbiter(Mapping.map(memberEntity)).build();
            tournament.setMainArbiter(Mapping.map(memberEntity));
            updateTournament(tournamentUUID, tournament);
        }
    }

    public void addRTSArbiter(UUID tournamentUUID, UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (!memberEntity.getMemberPermissions().getArbiterNumber().isEmpty()) {
            Tournament tournament = Tournament.builder().commissionRTSArbiter(Mapping.map(memberEntity)).build();
            tournament.setCommissionRTSArbiter(Mapping.map(memberEntity));
            updateTournament(tournamentUUID, tournament);
        }
    }

    public void addNewCompetitionListToTournament(UUID tournamentUUID, UUID competitionUUID) {
        if (competitionUUID != null) {
            TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
            CompetitionEntity competition = competitionRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
            boolean exist = false;
            if (!tournamentEntity.getCompetitionsList().isEmpty()) {
                for (int i = 0; i < tournamentEntity.getCompetitionsList().size(); i++) {
                    if (tournamentEntity.getCompetitionsList().get(i).getName().equals(competition.getName())) {
                        exist = true;
                    }
                }
                LOG.info("Nie można dodać konkurencji bo taka już istnieje w zawodach");
            }
            if (!exist) {
                CompetitionMembersListEntity competitionMembersList = CompetitionMembersListEntity.builder()
                        .name(competition.getName())
                        .attachedTo(tournamentEntity.getName())
                        .date(tournamentEntity.getDate())
                        .build();
                competitionMembersListRepository.saveAndFlush(competitionMembersList);
                List<CompetitionMembersListEntity> competitionsList = tournamentEntity.getCompetitionsList();
                competitionsList.add(competitionMembersList);
                tournamentRepository.saveAndFlush(tournamentEntity);
                LOG.info("Dodano konkurencję do zawodów");
            }

        }
    }
}
