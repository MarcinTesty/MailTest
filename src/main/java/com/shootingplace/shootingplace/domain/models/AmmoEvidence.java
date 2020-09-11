package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoEvidence {

    private String label;

    private LocalDate date;

    private List<Caliber> caliberList = new ArrayList<>();

    private FilesModel file;
}
