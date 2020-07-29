package com.shootingplace.shootingplace.domain.entities;

import com.shootingplace.shootingplace.domain.enums.ArbiterClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberPermissionsEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private String instructorNumber;

    private String shootingLeaderNumber;

    private String arbiterNumber;
    @Enumerated(EnumType.STRING)
    private ArbiterClass arbiterClass;
    private LocalDate arbiterPermissionValidThru;
}
