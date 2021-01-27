package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentDTO {

    private String name;
    private LocalDate date;
    private String tournamentUUID;

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

    public String getTournamentUUID() {
        return tournamentUUID;
    }

    public void setTournamentUUID(String tournamentUUID) {
        this.tournamentUUID = tournamentUUID;
    }
}
