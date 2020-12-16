package com.shootingplace.shootingplace.domain.enums;

public enum ArbiterWorkClass {
    MAIN_ARBITER("Sędzia Główny Zawodów"),
    RTS("Sędzia Komisji Obliczeniowej"),
    HELP("Sędzia Pomocniczy");

    private String name;

    ArbiterWorkClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



}
