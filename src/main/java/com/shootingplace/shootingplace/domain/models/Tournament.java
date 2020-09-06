package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tournament {

    private String name;
    private LocalDate date;


    private Member commissionRTSArbiter;

    private Member mainArbiter;

    private Boolean open;


}
