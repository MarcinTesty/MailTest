package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalEvidenceEntity {

    @Id
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "uuid")
    private String uuid;

    @OneToMany
    private List<AmmoUsedEntity> ammoList;

    public String getUuid() {
        return uuid;
    }

    public List<AmmoUsedEntity> getAmmoList() {
        return ammoList;
    }

    public void setAmmoList(List<AmmoUsedEntity> ammoList) {
        this.ammoList = ammoList;
    }
}
