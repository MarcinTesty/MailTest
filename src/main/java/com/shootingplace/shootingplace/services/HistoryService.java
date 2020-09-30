package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionHistoryEntity;
import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import com.shootingplace.shootingplace.domain.entities.HistoryEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.History;
import com.shootingplace.shootingplace.repositories.CompetitionHistoryRepository;
import com.shootingplace.shootingplace.repositories.HistoryRepository;
import com.shootingplace.shootingplace.repositories.LicenseRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
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
    private final Logger LOG = LogManager.getLogger(getClass());


    public HistoryService(HistoryRepository historyRepository, MemberRepository memberRepository, LicenseRepository licenseRepository, CompetitionHistoryRepository competitionHistoryRepository) {
        this.historyRepository = historyRepository;
        this.memberRepository = memberRepository;
        this.licenseRepository = licenseRepository;
        this.competitionHistoryRepository = competitionHistoryRepository;
    }

    void createHistory(UUID memberUUID, History history) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = Mapping.map(history);
        historyEntity.setContributionRecord(new LocalDate[]{LocalDate.now()});
        historyEntity.setLicenseHistory(new String[3]);
        historyEntity.setPatentDay(new LocalDate[3]);
        historyEntity.setPistolCounter(0);
        historyEntity.setRifleCounter(0);
        historyEntity.setShotgunCounter(0);
        historyRepository.saveAndFlush(historyEntity);
        memberEntity.setHistory(historyEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Historia została utworzona");
    }


    CompetitionHistoryEntity createCompetitionHistoryEntity(String name,LocalDate date,String discipline) {


        CompetitionHistoryEntity competitionHistoryEntity = CompetitionHistoryEntity.builder()
                .name(name)
                .date(date)
                .discipline(discipline)
                .build();
        return competitionHistoryEntity;

    }

    void addContributionRecord(UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate[] newState = new LocalDate[historyEntity.getContributionRecord().length + 1];

        for (int i = 0; i <= historyEntity.getContributionRecord().length - 1; i++) {
            newState[i] = historyEntity.getContributionRecord()[i];
            newState[i + 1] = LocalDate.now();
        }
        LOG.info("Dodano rekord w historii składek");
        LocalDate[] sortState = selectionSort(newState);
        historyEntity.setContributionRecord(sortState);
        historyRepository.saveAndFlush(historyEntity);
    }

    public Boolean addContributionRecord(UUID memberUUID, String date) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate parsedDate = LocalDate.parse(date);
        if (parsedDate.isAfter(LocalDate.now())) {
            LOG.info("Nie dodano rekordu w historii składek - data z przyszłości");
            return false;
        }
        LocalDate[] newState = new LocalDate[historyEntity.getContributionRecord().length + 1];

        for (int i = 0; i <= historyEntity.getContributionRecord().length - 1; i++) {
            newState[i] = historyEntity.getContributionRecord()[i];
            newState[i + 1] = LocalDate.parse(date);
        }
        LOG.info("Dodano rekord w historii składek");
        LocalDate[] sortState = selectionSort(newState);
        historyEntity.setContributionRecord(sortState);
        historyRepository.saveAndFlush(historyEntity);
        return true;
    }

    void addLicenseHistoryRecord(UUID memberUUID, int index) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
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
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
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
        return true;
    }

    public Boolean addLicenseHistoryPaymentRecord(UUID memberUUID, LocalDate date) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate[] newState = new LocalDate[historyEntity.getLicensePaymentHistory().length + 1];

        for (int i = 0; i <= historyEntity.getLicensePaymentHistory().length - 1; i++) {
            newState[i] = historyEntity.getLicensePaymentHistory()[i];
            newState[i + 1] = date;
        }
        LocalDate[] sortState = selectionSort(newState);
        historyEntity.setContributionRecord(sortState);
        historyRepository.saveAndFlush(historyEntity);
        return true;
    }


    void addCompetitionRecord(UUID memberUUID, CompetitionMembersListEntity list) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        if (historyEntity.getCompetitionHistory() != null) {
            List<CompetitionHistoryEntity> competitionHistory = historyEntity.getCompetitionHistory();
            CompetitionHistoryEntity competitionHistoryEntity = createCompetitionHistoryEntity(list.getAttachedTo(), list.getDate(), list.getName().toUpperCase().substring(0, 1));
            competitionHistoryRepository.saveAndFlush(competitionHistoryEntity);
            competitionHistory.add(competitionHistoryEntity);
            competitionHistory.sort(Comparator.comparing(CompetitionHistoryEntity::getDate));
            historyEntity.setCompetitionHistory(competitionHistory);

//            if (memberEntity.getLicense().getValidThru().isBefore(LocalDate.of(LocalDate.now().getYear(), 12, 31))) {
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
//                }
            }

            LOG.info("Dodano wpis w historii startów");
            historyRepository.saveAndFlush(historyEntity);
//        } else {
//            List<CompetitionHistoryEntity> competitionHistory = new ArrayList<>();
//            CompetitionHistoryEntity competitionHistoryEntity = createCompetitionHistoryEntity(list.getAttachedTo(), list.getDate(), list.getName().toUpperCase().substring(0, 1));
//            competitionHistoryRepository.saveAndFlush(competitionHistoryEntity);
//            competitionHistory.add(competitionHistoryEntity);
//            historyEntity.setCompetitionHistory(competitionHistory);

//            String[] newState = new String[1];
//            newState[0] = list.getAttachedTo().concat(" " + list.getName().toUpperCase().substring(0, 1));
//            sort(newState);
//            reverse(newState);
//            historyEntity.setCompetitionHistory(newState);
//            if (memberEntity.getLicense().getValidThru().isBefore(LocalDate.of(LocalDate.now().getYear(), 12, 31))) {
//            if (list.getName().toUpperCase().startsWith("P")) {
//                Integer pistolCounter = historyEntity.getPistolCounter() + 1;
//                historyEntity.setPistolCounter(pistolCounter);
//            }
//            if (list.getName().toUpperCase().startsWith("K")) {
//                Integer rifleCounter = historyEntity.getRifleCounter() + 1;
//                historyEntity.setRifleCounter(rifleCounter);
//            }
//            if (list.getName().toUpperCase().startsWith("S")) {
//                Integer shotgunCounter = historyEntity.getShotgunCounter() + 1;
//                historyEntity.setShotgunCounter(shotgunCounter);
////                }
//            }
//
//            LOG.info("Dodano wpis w historii startów");
//            historyRepository.saveAndFlush(historyEntity);
        }

        if (!memberEntity.getLicense().getCanProlong() && (historyEntity.getPistolCounter() >= 4 || historyEntity.getRifleCounter() >= 4 || historyEntity.getShotgunCounter() >= 4)) {
            if (historyEntity.getPistolCounter() >= 4 && (historyEntity.getRifleCounter() >= 2 && historyEntity.getShotgunCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());
            }
            if (historyEntity.getRifleCounter() >= 4 && (historyEntity.getPistolCounter() >= 2 && historyEntity.getShotgunCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());
            }
            if (historyEntity.getShotgunCounter() >= 4 && (historyEntity.getRifleCounter() >= 2 && historyEntity.getPistolCounter() >= 2)) {
                memberEntity.getLicense().setCanProlong(true);
                licenseRepository.saveAndFlush(memberEntity.getLicense());
            }
        }
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

    private void reverse(String[] array) {
        Collections.reverse(Arrays.asList(array));
    }
}
