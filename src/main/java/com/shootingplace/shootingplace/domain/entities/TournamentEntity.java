package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    @OneToOne(orphanRemoval = true)
    private MemberEntity commissionRTSArbiter;
    @OneToOne(orphanRemoval = true)
    private MemberEntity mainArbiter;
    @OneToMany
    private Set<MemberEntity> arbitersList;
    @OneToMany(orphanRemoval = true)
    private List<CompetitionMembersListEntity> competitionsList = new ArrayList<>();
    private Boolean open;


}
