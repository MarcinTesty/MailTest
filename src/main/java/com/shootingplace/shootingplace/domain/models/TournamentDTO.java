package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentDTO {

    private String name;
    private LocalDate date;
    private UUID tournamentUUID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public UUID getTournamentUUID() {
        return tournamentUUID;
    }

    public void setTournamentUUID(UUID tournamentUUID) {
        this.tournamentUUID = tournamentUUID;
    }
}
