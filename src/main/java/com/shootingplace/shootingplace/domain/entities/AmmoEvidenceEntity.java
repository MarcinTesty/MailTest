package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoEvidenceEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private String label;

    private LocalDate date;
    @OneToMany
    private List<CaliberEntity> caliberList = new ArrayList<>();
    @OneToOne
    private FilesEntity file;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<CaliberEntity> getCaliberList() {
        return caliberList;
    }

    public void setCaliberList(List<CaliberEntity> caliberList) {
        this.caliberList = caliberList;
    }

    public FilesEntity getFile() {
        return file;
    }

    public void setFile(FilesEntity file) {
        this.file = file;
    }
}
