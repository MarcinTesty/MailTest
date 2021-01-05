package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtherPersonEntity {


    @Id
    private Integer id;

    private String firstName;
    private String secondName;
    @OneToOne
    private ClubEntity club;
    @OneToOne
    private MemberPermissionsEntity permissionsEntity;

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public ClubEntity getClub() {
        return club;
    }

    public void setClub(ClubEntity club) {
        this.club = club;
    }

    public MemberPermissionsEntity getPermissionsEntity() {
        return permissionsEntity;
    }

    public void setPermissionsEntity(MemberPermissionsEntity permissionsEntity) {
        this.permissionsEntity = permissionsEntity;
    }
}
