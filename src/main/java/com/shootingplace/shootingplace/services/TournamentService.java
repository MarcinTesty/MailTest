package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.enums.ArbiterWorkClass;
import com.shootingplace.shootingplace.domain.models.Tournament;
import com.shootingplace.shootingplace.domain.models.TournamentDTO;
import com.shootingplace.shootingplace.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final MemberRepository memberRepository;
    private final OtherPersonRepository otherPersonRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;
    private final CompetitionRepository competitionRepository;
    private final HistoryService historyService;
    private final Logger LOG = LogManager.getLogger(getClass());


    public TournamentService(TournamentRepository tournamentRepository, MemberRepository memberRepository, OtherPersonRepository otherPersonRepository, CompetitionMembersListRepository competitionMembersListRepository, CompetitionRepository competitionRepository, HistoryService historyService) {
        this.tournamentRepository = tournamentRepository;
        this.memberRepository = memberRepository;
        this.otherPersonRepository = otherPersonRepository;
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
        if (tournamentEntity.isOpen()) {
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
        return tournamentRepository
                .findAll()
                .stream()
                .filter(TournamentEntity::isOpen)
                .sorted(Comparator.comparing(TournamentEntity::getDate)
                        .reversed()).collect(Collectors.toList());
    }

    public boolean closeTournament(UUID tournamentUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {
            LOG.info("Zawody " + tournamentEntity.getName() + " zostały zamknięte");
            tournamentEntity.setOpen(false);
            tournamentRepository.saveAndFlush(tournamentEntity);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeArbiterFromTournament(UUID tournamentUUID, int legitimationNumber) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {
            String function = ArbiterWorkClass.HELP.getName();

            MemberEntity memberEntity = memberRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getLegitimationNumber()
                            .equals(legitimationNumber))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            List<MemberEntity> list = tournamentEntity.getArbitersList();

            list.remove(memberEntity);

            tournamentRepository.saveAndFlush(tournamentEntity);

            historyService.removeJudgingRecord(memberEntity.getUuid(), tournamentUUID, function);
            return true;
        }
        return false;
    }

    public boolean addMainArbiter(UUID tournamentUUID, int legitimationNumber) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {

            if (tournamentEntity.getOtherMainArbiter() != null) {
                tournamentEntity.setOtherMainArbiter(null);
            }
            String function = ArbiterWorkClass.MAIN_ARBITER.getName();

            MemberEntity memberEntity = memberRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getLegitimationNumber()
                            .equals(legitimationNumber))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            if (tournamentEntity.getCommissionRTSArbiter() != null) {
                if (tournamentEntity.getCommissionRTSArbiter().equals(memberEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getArbitersList() != null) {
                if (tournamentEntity.getArbitersList().stream().anyMatch(a -> a.equals(memberEntity))) {
                    return false;
                }
            }

            if (!memberEntity.getMemberPermissions().getArbiterNumber().isEmpty()) {
                if (tournamentEntity.getMainArbiter() == null || tournamentEntity.getMainArbiter() != memberEntity) {
                    if (tournamentEntity.getMainArbiter() == null) {
                        tournamentEntity.setMainArbiter(memberEntity);

                    } else {
                        historyService.removeJudgingRecord(tournamentEntity.getMainArbiter().getUuid(), tournamentUUID, function);
                        tournamentEntity.setMainArbiter(memberEntity);
                        tournamentEntity.setOtherMainArbiter(null);

                    }
                    tournamentRepository.saveAndFlush(tournamentEntity);
                    LOG.info("Ustawiono sędziego głównego zawodów");
                    historyService.addJudgingRecord(memberEntity.getUuid(), tournamentUUID, function);
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean addOtherMainArbiter(UUID tournamentUUID, int id) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {
            String function = ArbiterWorkClass.MAIN_ARBITER.getName();

            OtherPersonEntity otherPersonEntity = otherPersonRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getId()
                            .equals(id))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            if (tournamentEntity.getOtherCommissionRTSArbiter() != null) {
                if (tournamentEntity.getOtherCommissionRTSArbiter().equals(otherPersonEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getOtherArbitersList() != null) {
                if (tournamentEntity.getOtherArbitersList().stream().anyMatch(a -> a.equals(otherPersonEntity))) {
                    return false;
                }
            }

            if (!otherPersonEntity.getPermissionsEntity().getArbiterNumber().isEmpty()) {
                if (tournamentEntity.getOtherMainArbiter() == null || tournamentEntity.getOtherMainArbiter() != otherPersonEntity) {
                    if (tournamentEntity.getOtherMainArbiter() == null) {
                        historyService.removeJudgingRecord(tournamentEntity.getMainArbiter().getUuid(), tournamentUUID, function);
                        tournamentEntity.setOtherMainArbiter(otherPersonEntity);
                        tournamentEntity.setMainArbiter(null);

                    } else {
                        historyService.removeJudgingRecord(tournamentEntity.getMainArbiter().getUuid(), tournamentUUID, function);
                        tournamentEntity.setMainArbiter(null);
                        tournamentEntity.setOtherMainArbiter(otherPersonEntity);
                    }
                    tournamentRepository.saveAndFlush(tournamentEntity);
                    LOG.info("Ustawiono sędziego głównego zawodów");
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean addRTSArbiter(UUID tournamentUUID, int legitimationNumber) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {

            if (tournamentEntity.getOtherCommissionRTSArbiter() != null) {
                tournamentEntity.setOtherCommissionRTSArbiter(null);
            }
            String function = ArbiterWorkClass.RTS_ARBITER.getName();

            MemberEntity memberEntity = memberRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getLegitimationNumber()
                            .equals(legitimationNumber))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            if (tournamentEntity.getMainArbiter() != null) {
                if (tournamentEntity.getMainArbiter().equals(memberEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getArbitersList() != null) {
                if (tournamentEntity.getArbitersList().stream().anyMatch(a -> a.equals(memberEntity))) {
                    return false;
                }
            }

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
                    historyService.addJudgingRecord(memberEntity.getUuid(), tournamentUUID, function);
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean addOtherRTSArbiter(UUID tournamentUUID, int id) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {

            String function = ArbiterWorkClass.RTS_ARBITER.getName();

            OtherPersonEntity otherPersonEntity = otherPersonRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getId()
                            .equals(id))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            if (tournamentEntity.getOtherMainArbiter() != null) {
                if (tournamentEntity.getOtherMainArbiter().equals(otherPersonEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getOtherArbitersList() != null) {
                if (tournamentEntity.getOtherArbitersList().stream().anyMatch(a -> a.equals(otherPersonEntity))) {
                    return false;
                }
            }

            if (!otherPersonEntity.getPermissionsEntity().getArbiterNumber().isEmpty()) {
                if (tournamentEntity.getOtherCommissionRTSArbiter() == null || tournamentEntity.getOtherCommissionRTSArbiter() != otherPersonEntity) {
                    if (tournamentEntity.getOtherCommissionRTSArbiter() == null) {
                        historyService.removeJudgingRecord(tournamentEntity.getCommissionRTSArbiter().getUuid(), tournamentUUID, function);
                        tournamentEntity.setOtherCommissionRTSArbiter(otherPersonEntity);
                        tournamentEntity.setCommissionRTSArbiter(null);

                    } else {
                        historyService.removeJudgingRecord(tournamentEntity.getCommissionRTSArbiter().getUuid(), tournamentUUID, function);
                        tournamentEntity.setOtherCommissionRTSArbiter(otherPersonEntity);
                        tournamentEntity.setCommissionRTSArbiter(null);
                    }
                    tournamentRepository.saveAndFlush(tournamentEntity);
                    LOG.info("Ustawiono sędziego biura obliczeń");
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean addOthersArbiters(UUID tournamentUUID, int legitimationNumber) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {
            String function = ArbiterWorkClass.HELP.getName();

            MemberEntity memberEntity = memberRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getLegitimationNumber()
                            .equals(legitimationNumber))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            if (tournamentEntity.getMainArbiter() != null) {
                if (tournamentEntity.getMainArbiter().equals(memberEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getCommissionRTSArbiter() != null) {
                if (tournamentEntity.getCommissionRTSArbiter().equals(memberEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getArbitersList() != null) {
                if (tournamentEntity.getArbitersList().contains(memberEntity)) {
                    return false;
                }
            }
            if (!memberEntity.getMemberPermissions().getArbiterNumber().isEmpty()) {
                List<MemberEntity> list = tournamentEntity.getArbitersList();
                list.add(memberEntity);
                list.sort(Comparator.comparing(MemberEntity::getSecondName));

                tournamentEntity.setArbitersList(list);
                tournamentRepository.saveAndFlush(tournamentEntity);
                LOG.info("Dodano sędziego pomocniczego");
                historyService.addJudgingRecord(memberEntity.getUuid(), tournamentUUID, function);
            }
            return true;
        }
        return false;
    }

    public boolean addPersonOthersArbiters(UUID tournamentUUID, int id) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {

            OtherPersonEntity otherPersonEntity = otherPersonRepository
                    .findAll()
                    .stream()
                    .filter(f -> f.getId()
                            .equals(id))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            if (tournamentEntity.getOtherMainArbiter() != null) {
                if (tournamentEntity.getOtherMainArbiter().equals(otherPersonEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getOtherCommissionRTSArbiter() != null) {
                if (tournamentEntity.getOtherCommissionRTSArbiter().equals(otherPersonEntity)) {
                    return false;
                }
            }
            if (tournamentEntity.getOtherArbitersList() != null) {
                if (tournamentEntity.getOtherArbitersList().contains(otherPersonEntity)) {
                    return false;
                }
            }
            if (!otherPersonEntity.getPermissionsEntity().getArbiterNumber().isEmpty()) {
                List<OtherPersonEntity> list = tournamentEntity.getOtherArbitersList();
                list.add(otherPersonEntity);
                list.sort(Comparator.comparing(OtherPersonEntity::getSecondName));

                tournamentEntity.setOtherArbitersList(list);
                tournamentRepository.saveAndFlush(tournamentEntity);
                LOG.info("Dodano sędziego pomocniczego");
            }
            return true;
        }
        return false;
    }

    public boolean addNewCompetitionListToTournament(UUID tournamentUUID, UUID competitionUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        if (tournamentEntity.isOpen()) {
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
                        .discipline(competition.getDiscipline())
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

    public List<TournamentDTO> getClosedTournaments() {
        List<TournamentEntity> all = tournamentRepository.findAll().stream().filter(f -> !f.isOpen()).collect(Collectors.toList());
        List<TournamentDTO> allDTO = new ArrayList<>();
        all.forEach(e -> allDTO.add(Mapping.map1(e)));
        allDTO.sort(Comparator.comparing(TournamentDTO::getDate).reversed());
        return allDTO;
    }
}
