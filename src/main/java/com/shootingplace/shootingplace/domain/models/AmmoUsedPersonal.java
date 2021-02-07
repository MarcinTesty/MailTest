package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoUsedPersonal {
    private String caliberName;

    private String memberUUID;

    private Integer counter;

    private String caliberUUID;

    public String getCaliberName() {
        return caliberName;
    }

    public void setCaliberName(String caliberName) {
        this.caliberName = caliberName;
    }

    public String getMemberUUID() {
        return memberUUID;
    }

    public void setMemberUUID(String memberUUID) {
        this.memberUUID = memberUUID;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public String getCaliberUUID() {
        return caliberUUID;
    }

    public void setCaliberUUID(String caliberUUID) {
        this.caliberUUID = caliberUUID;
    }
}
