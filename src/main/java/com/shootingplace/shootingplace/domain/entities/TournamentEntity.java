package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private String name;
    private LocalDate date;

    @ManyToOne
    private MemberEntity mainArbiter;
    @ManyToOne
    private MemberEntity commissionRTSArbiter;
    @ManyToMany
    private List<MemberEntity> arbitersList;

    @ManyToOne
    private OtherPersonEntity otherMainArbiter;
    @ManyToOne
    private OtherPersonEntity otherCommissionRTSArbiter;
    @ManyToMany
    private List<OtherPersonEntity> otherArbitersList;


    @OneToMany
    private List<CompetitionMembersListEntity> competitionsList = new ArrayList<>();
    private boolean open;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MemberEntity getCommissionRTSArbiter() {
        return commissionRTSArbiter;
    }

    public void setCommissionRTSArbiter(MemberEntity commissionRTSArbiter) {
        this.commissionRTSArbiter = commissionRTSArbiter;
    }

    public MemberEntity getMainArbiter() {
        return mainArbiter;
    }

    public void setMainArbiter(MemberEntity mainArbiter) {
        this.mainArbiter = mainArbiter;
    }

    public List<MemberEntity> getArbitersList() {
        return arbitersList;
    }

    public void setArbitersList(List<MemberEntity> arbitersList) {
        this.arbitersList = arbitersList;
    }

    public List<CompetitionMembersListEntity> getCompetitionsList() {
        return competitionsList;
    }

    public void setCompetitionsList(List<CompetitionMembersListEntity> competitionsList) {
        this.competitionsList = competitionsList;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public OtherPersonEntity getOtherMainArbiter() {
        return otherMainArbiter;
    }

    public void setOtherMainArbiter(OtherPersonEntity otherMainArbiter) {
        this.otherMainArbiter = otherMainArbiter;
    }

    public OtherPersonEntity getOtherCommissionRTSArbiter() {
        return otherCommissionRTSArbiter;
    }

    public void setOtherCommissionRTSArbiter(OtherPersonEntity otherCommissionRTSArbiter) {
        this.otherCommissionRTSArbiter = otherCommissionRTSArbiter;
    }

    public List<OtherPersonEntity> getOtherArbitersList() {
        return otherArbitersList;
    }

    public void setOtherArbitersList(List<OtherPersonEntity> otherArbitersList) {
        this.otherArbitersList = otherArbitersList;
    }
}
