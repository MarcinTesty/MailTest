package com.shootingplace.shootingplace.domain.models;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoUsedEvidence {
    private String caliberName;

    private String userName;

    private MemberEntity memberEntity;

    private OtherPersonEntity otherPersonEntity;

    private Integer counter;

    private UUID caliberUUID;

    public UUID getCaliberUUID() {
        return caliberUUID;
    }

    public void setCaliberUUID(UUID caliberUUID) {
        this.caliberUUID = caliberUUID;
    }

    public String getCaliberName() {
        return caliberName;
    }

    public void setCaliberName(String caliberName) {
        this.caliberName = caliberName;
    }

    public MemberEntity getMemberEntity() {
        return memberEntity;
    }

    public void setMemberEntity(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public OtherPersonEntity getOtherPersonEntity() {
        return otherPersonEntity;
    }

    public void setOtherPersonEntity(OtherPersonEntity otherPersonEntity) {
        this.otherPersonEntity = otherPersonEntity;
    }
}
