package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoEvidenceEntity {

    @Id
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "org.hibernate.id.UUIDGenerator")
    private String uuid;

    private LocalDate date;

    private String number;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<AmmoInEvidenceEntity> ammoInEvidenceEntityList;

    private boolean open;

    @OneToOne(cascade = CascadeType.ALL)
    private FilesEntity file;

    public String getUuid() {
        return uuid;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public FilesEntity getFile() {
        return file;
    }

    public void setFile(FilesEntity file) {
        this.file = file;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<AmmoInEvidenceEntity> getAmmoInEvidenceEntityList() {
        return ammoInEvidenceEntityList;
    }

    public void setAmmoInEvidenceEntityList(List<AmmoInEvidenceEntity> ammoInEvidenceEntityList) {
        this.ammoInEvidenceEntityList = ammoInEvidenceEntityList;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
