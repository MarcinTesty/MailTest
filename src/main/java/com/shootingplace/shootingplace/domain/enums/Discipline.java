package com.shootingplace.shootingplace.domain.enums;

public enum Discipline {
    PISTOL("Pistolet"),
    RIFLE("Karabin"),
    SHOTGUN("Strzelba");

    private String name;

    Discipline(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
