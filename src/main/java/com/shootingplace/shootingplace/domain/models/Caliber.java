package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Caliber {

    private String name;

    private Integer quantity;
    private Integer[] ammoUsed;

    private List<Member> members = new ArrayList<>();

}
