package com.shootingplace.shootingplace.controllers;

import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.FilesEntity;
import com.shootingplace.shootingplace.services.FilesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FilesController {

    private final FilesService filesService;


    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping("/downloadContribution/{memberUUID}")
    public ResponseEntity<byte[]> getContributionFile(@PathVariable String memberUUID) throws IOException, DocumentException {
        FilesEntity filesEntity = filesService.contributionConfirm(memberUUID);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }

    @GetMapping("/downloadPersonalCard/{memberUUID}")
    public ResponseEntity<byte[]> getPersonalCardFile(@PathVariable String memberUUID) throws IOException, DocumentException {
        FilesEntity filesEntity = filesService.personalCardFile(memberUUID);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }

    @GetMapping("/downloadAmmunitionList/{ammoEvidenceUUID}")
    public ResponseEntity<byte[]> getAmmoListFile(@PathVariable String ammoEvidenceUUID) throws IOException, DocumentException {
        FilesEntity filesEntity = filesService.createAmmunitionListDocument(ammoEvidenceUUID);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }

    @GetMapping("/downloadApplication/{memberUUID}")
    public ResponseEntity<byte[]> getApplicationForExtensionOfTheCompetitorsLicense(@PathVariable String memberUUID) throws IOException, DocumentException {
        FilesEntity filesEntity = filesService.createApplicationForExtensionOfTheCompetitorsLicense(memberUUID);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }

    @GetMapping("/downloadAnnouncementFromCompetition/{tournamentUUID}")
    public ResponseEntity<byte[]> getAnnouncementFromCompetition(@PathVariable String tournamentUUID) throws IOException, DocumentException {
        FilesEntity filesEntity = filesService.createAnnouncementFromCompetition(tournamentUUID);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }

    @GetMapping("/downloadAllMembersToTable")
    public ResponseEntity<byte[]> getAllMembersToTable() throws IOException, DocumentException {
        FilesEntity filesEntity = filesService.getAllMembersToTable();
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }

    @GetMapping("/downloadCertificateOfClubMembership/{memberUUID}")
    public ResponseEntity<byte[]> CertificateOfClubMembership(@PathVariable String memberUUID) throws IOException, DocumentException {
        FilesEntity filesEntity = filesService.CertificateOfClubMembership(memberUUID);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }
    @GetMapping("/downloadMetric/{tournamentUUID}")
    public ResponseEntity<byte[]> getMemberMetrics(@RequestParam String memberUUID,@RequestParam String otherID,@PathVariable String tournamentUUID) throws IOException, DocumentException {
        if (otherID.equals("0")){
            otherID = null;
        }else{
            memberUUID = null;
        }

        FilesEntity filesEntity = filesService.getStartsMetric(memberUUID,otherID,tournamentUUID);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(filesEntity.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                    .body(filesEntity.getData());
        } finally {
            filesService.delete(filesEntity);
        }
    }


}
