package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;
    @OneToMany
    private List<ContributionEntity> contributionList;
    // do zrobienia
    private String[] licenseHistory;
    // do zrobienia
    private LocalDate[] licensePaymentHistory;

    private Boolean patentFirstRecord = false;
    private LocalDate[] patentDay;

    private Integer pistolCounter = 0;
    private Integer rifleCounter = 0;
    private Integer shotgunCounter = 0;
    @OneToMany(orphanRemoval = true)
    private List<CompetitionHistoryEntity> competitionHistory;

    @ManyToMany
    private List<JudgingHistoryEntity> judgingHistory;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<ContributionEntity> getContributionList() {
        return contributionList;
    }

    public void setContributionsList(List<ContributionEntity> contributionsList) {
        this.contributionList = contributionsList;
    }

    public String[] getLicenseHistory() {
        return licenseHistory;
    }

    public void setLicenseHistory(String[] licenseHistory) {
        this.licenseHistory = licenseHistory;
    }

    public LocalDate[] getLicensePaymentHistory() {
        return licensePaymentHistory;
    }

    public void setLicensePaymentHistory(LocalDate[] licensePaymentHistory) {
        this.licensePaymentHistory = licensePaymentHistory;
    }

    public Boolean getPatentFirstRecord() {
        return patentFirstRecord;
    }

    public void setPatentFirstRecord(Boolean patentFirstRecord) {
        this.patentFirstRecord = patentFirstRecord;
    }

    public LocalDate[] getPatentDay() {
        return patentDay;
    }

    public void setPatentDay(LocalDate[] patentDay) {
        this.patentDay = patentDay;
    }

    public Integer getPistolCounter() {
        return pistolCounter;
    }

    public void setPistolCounter(Integer pistolCounter) {
        this.pistolCounter = pistolCounter;
    }

    public Integer getRifleCounter() {
        return rifleCounter;
    }

    public void setRifleCounter(Integer rifleCounter) {
        this.rifleCounter = rifleCounter;
    }

    public Integer getShotgunCounter() {
        return shotgunCounter;
    }

    public void setShotgunCounter(Integer shotgunCounter) {
        this.shotgunCounter = shotgunCounter;
    }

    public List<CompetitionHistoryEntity> getCompetitionHistory() {
        return competitionHistory;
    }

    public void setCompetitionHistory(List<CompetitionHistoryEntity> competitionHistory) {
        this.competitionHistory = competitionHistory;
    }

    public List<JudgingHistoryEntity> getJudgingHistory() {
        return judgingHistory;
    }

    public void setJudgingHistory(List<JudgingHistoryEntity> judgingHistory) {
        this.judgingHistory = judgingHistory;
    }
}
