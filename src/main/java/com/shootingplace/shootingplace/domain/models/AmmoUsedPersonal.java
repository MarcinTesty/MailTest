package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoUsedPersonal {
    private String caliberName;

    private UUID memberUUID;

    private Integer counter;

    private UUID caliberUUID;

    public String getCaliberName() {
        return caliberName;
    }

    public void setCaliberName(String caliberName) {
        this.caliberName = caliberName;
    }

    public UUID getMemberUUID() {
        return memberUUID;
    }

    public void setMemberUUID(UUID memberUUID) {
        this.memberUUID = memberUUID;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public UUID getCaliberUUID() {
        return caliberUUID;
    }

    public void setCaliberUUID(UUID caliberUUID) {
        this.caliberUUID = caliberUUID;
    }
}
