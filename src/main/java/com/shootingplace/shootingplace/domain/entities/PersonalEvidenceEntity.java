package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    private String[] ammo;
    @OneToOne
    private FilesEntity file;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String[] getAmmo() {
        return ammo;
    }

    public void setAmmo(String[] ammo) {
        this.ammo = ammo;
    }

    public FilesEntity getFile() {
        return file;
    }

    public void setFile(FilesEntity file) {
        this.file = file;
    }
}
