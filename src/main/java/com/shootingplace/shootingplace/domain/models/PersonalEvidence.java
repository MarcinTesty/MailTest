package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalEvidence {

    private String[] ammo;
    private FilesModel file;

    public String[] getAmmo() {
        return ammo;
    }

    public void setAmmo(String[] ammo) {
        this.ammo = ammo;
    }

    public FilesModel getFile() {
        return file;
    }

    public void setFile(FilesModel file) {
        this.file = file;
    }
}
