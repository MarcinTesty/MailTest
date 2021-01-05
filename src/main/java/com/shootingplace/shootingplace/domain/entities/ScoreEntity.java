package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private float score;

    private float innerTen;
    private float outerTen;

    private String name;

    private boolean ammunition;

    private UUID competitionMembersListEntityUUID;
    @OneToOne(orphanRemoval = true)
    private MemberEntity member;
    @OneToOne(orphanRemoval = true)
    private OtherPersonEntity otherPersonEntity;

    public UUID getUuid() {
        return uuid;
    }

    public float getScore() {
        return score;
    }

    public float getInnerTen() {
        return innerTen;
    }

    public void setInnerTen(float innerTen) {
        this.innerTen = innerTen;
    }

    public float getOuterTen() {
        return outerTen;
    }

    public void setOuterTen(float outerTen) {
        this.outerTen = outerTen;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public UUID getCompetitionMembersListEntityUUID() {
        return competitionMembersListEntityUUID;
    }

    public void setCompetitionMembersListEntityUUID(UUID competitionMembersListEntityUUID) {
        this.competitionMembersListEntityUUID = competitionMembersListEntityUUID;
    }

    public MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    public OtherPersonEntity getOtherPersonEntity() {
        return otherPersonEntity;
    }

    public void setOtherPersonEntity(OtherPersonEntity otherPersonEntity) {
        this.otherPersonEntity = otherPersonEntity;
    }

    public boolean isAmmunition() {
        return ammunition;
    }

    public void toggleAmmunition() {
        this.ammunition = !this.ammunition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
