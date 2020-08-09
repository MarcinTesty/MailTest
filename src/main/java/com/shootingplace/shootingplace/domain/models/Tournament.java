package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tournament {

    private String name;
    private LocalDate date;

    private Set<Member> members = new HashSet<>();

    private Set<Member> lineArbiters = new HashSet<>();

    private Member commissionRTSArbiter;

    private Member mainArbiter;

    private Boolean open;

    private List<Competition> competitionList;

    private Map<Competition,Member> map;


}
