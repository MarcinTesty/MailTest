package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private LocalDate[] contributionRecord;

    private String[] licenseHistory;
    private LocalDate[] licensePaymentHistory;
    private Boolean patentFirstRecord = false;
    private LocalDate[] patentDay;

}
