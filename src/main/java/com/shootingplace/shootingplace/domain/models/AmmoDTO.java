package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoDTO {

    private UUID evidenceUUID;
    private LocalDate date;

    public UUID getEvidenceUUID() {
        return evidenceUUID;
    }

    public void setEvidenceUUID(UUID evidenceUUID) {
        this.evidenceUUID = evidenceUUID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
