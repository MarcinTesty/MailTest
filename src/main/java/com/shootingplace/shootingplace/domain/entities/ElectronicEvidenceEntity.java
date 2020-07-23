package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ElectronicEvidenceEntity {

    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany
    @JoinColumn(name="member_id")
    private Set<MemberEntity> members = new HashSet<>();
    private String others;
    private LocalDate date;
}
