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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private String name;
    private LocalDate date;
    @OneToMany
    private Set<MemberEntity> members = new HashSet<>();
    @OneToMany
    private Set<MemberEntity> lineArbiters = new HashSet<>();
    @OneToOne(orphanRemoval = true)
    private MemberEntity commissionRTSArbiter;
    @OneToOne(orphanRemoval = true)
    private MemberEntity mainArbiter;
    private Boolean open;
    @ManyToMany
    private List<CompetitionEntity> competitionList;
    @OneToMany
    private Map<CompetitionEntity,MemberEntity> map;

}
