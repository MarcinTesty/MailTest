package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberPermissionsEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private String instructorNumber;

    private String shootingLeaderNumber;

    private String arbiterNumber;

    private String arbiterClass;
    private LocalDate arbiterPermissionValidThru;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getInstructorNumber() {
        return instructorNumber;
    }

    public void setInstructorNumber(String instructorNumber) {
        this.instructorNumber = instructorNumber;
    }

    public String getShootingLeaderNumber() {
        return shootingLeaderNumber;
    }

    public void setShootingLeaderNumber(String shootingLeaderNumber) {
        this.shootingLeaderNumber = shootingLeaderNumber;
    }

    public String getArbiterNumber() {
        return arbiterNumber;
    }

    public void setArbiterNumber(String arbiterNumber) {
        this.arbiterNumber = arbiterNumber;
    }

    public String getArbiterClass() {
        return arbiterClass;
    }

    public void setArbiterClass(String arbiterClass) {
        this.arbiterClass = arbiterClass;
    }

    public LocalDate getArbiterPermissionValidThru() {
        return arbiterPermissionValidThru;
    }

    public void setArbiterPermissionValidThru(LocalDate arbiterPermissionValidThru) {
        this.arbiterPermissionValidThru = arbiterPermissionValidThru;
    }
}
