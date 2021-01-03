package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoUsedEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private String caliberName;

    private UUID caliberUUID;

    private UUID memberUUID;

    private Integer otherPersonEntityID;

    private String userName;

    private Integer counter;

    public UUID getUuid() {
        return uuid;
    }


    public String getCaliberName() {
        return caliberName;
    }

    public void setCaliberName(String caliberName) {
        this.caliberName = caliberName;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public UUID getCaliberUUID() {
        return caliberUUID;
    }

    public void setCaliberUUID(UUID caliberUUID) {
        this.caliberUUID = caliberUUID;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getOtherPersonEntityID() {
        return otherPersonEntityID;
    }

    public void setOtherPersonEntityID(Integer otherPersonEntityID) {
        this.otherPersonEntityID = otherPersonEntityID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
