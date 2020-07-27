package com.shootingplace.shootingplace.domain.models;

import com.shootingplace.shootingplace.domain.enums.ArbiterClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberPermissions {

    private String instructorNumber;

    private String shootingLeaderNumber;

    private String arbiterNumber;
    private ArbiterClass arbiterClass;
    private LocalDate arbiterPermissionValidThru;

}