package com.shootingplace.shootingplace.domain.Entities;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String secondName;
    private String licenseNumber;
    @NotNull
    @NotEmpty
    private String email;
    @NotNull
    @NotEmpty
    private String pesel;
    private String address;
}
