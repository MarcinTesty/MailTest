package com.shootingplace.shootingplace.domain.models;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoUsedEvidence {
    private String caliberName;

    private MemberEntity memberUUID;

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

    public MemberEntity getMemberUUID() {
        return memberUUID;
    }

    public void setMemberUUID(MemberEntity memberUUID) {
        this.memberUUID = memberUUID;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
