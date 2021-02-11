package com.shootingplace.shootingplace.domain.enums;

public enum ArbiterWorkClass {
    MAIN_ARBITER("Sędzia Główny Zawodów"),
    RTS_ARBITER("Sędzia Komisji Obliczeniowej"),
    HELP("Sędzia Stanowiskowy"),
    RTS_HELP("Sędzia Biura Obliczeń");

    private String name;

    ArbiterWorkClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



}
