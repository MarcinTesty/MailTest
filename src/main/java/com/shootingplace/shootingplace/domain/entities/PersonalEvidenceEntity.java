package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalEvidenceEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    @OneToOne
    private FilesEntity file;
    @OneToMany
    private List<AmmoUsedEntity> ammoList;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public FilesEntity getFile() {
        return file;
    }

    public void setFile(FilesEntity file) {
        this.file = file;
    }

    public List<AmmoUsedEntity> getAmmoList() {
        return ammoList;
    }

    public void setAmmoList(List<AmmoUsedEntity> ammoList) {
        this.ammoList = ammoList;
    }
}
