package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoEvidence {

    private String label;

    private LocalDate date;

    private List<Caliber> caliberList = new ArrayList<>();

    private FilesModel file;

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

    public List<Caliber> getCaliberList() {
        return caliberList;
    }

    public void setCaliberList(List<Caliber> caliberList) {
        this.caliberList = caliberList;
    }

    public FilesModel getFile() {
        return file;
    }

    public void setFile(FilesModel file) {
        this.file = file;
    }
}
