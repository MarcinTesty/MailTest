package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Club {

    private String name;
    private String licenseNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
