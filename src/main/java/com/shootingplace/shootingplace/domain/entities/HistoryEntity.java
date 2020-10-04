package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;
    private LocalDate[] contributionRecord;

    private String[] licenseHistory;
    private LocalDate[] licensePaymentHistory;

    private Boolean patentFirstRecord = false;
    private LocalDate[] patentDay;

    private Integer pistolCounter = 0;
    private Integer rifleCounter = 0;
    private Integer shotgunCounter = 0;
    @OneToMany
    private List<CompetitionHistoryEntity> competitionHistory;

    @ManyToMany
    private List<JudgingHistoryEntity> judgingHistory;
}
