package com.shootingplace.shootingplace.domain.models;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ElectronicEvidence {

    private LocalDate date;

    private Set<Member> members = new HashSet<>();

}
