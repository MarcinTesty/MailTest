package com.shootingplace.shootingplace.controllers;

import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.FilesEntity;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.services.FilesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FilesController {

    private final MemberRepository memberRepository;
    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final FilesService filesService;


    public FilesController(MemberRepository memberRepository, AmmoEvidenceRepository ammoEvidenceRepository, FilesService filesService) {
        this.memberRepository = memberRepository;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
        this.filesService = filesService;
    }

    @GetMapping("/downloadContribution/{memberUUID}")
    public ResponseEntity<byte[]> getContributionFile(@PathVariable UUID memberUUID) throws IOException, DocumentException {
        filesService.contributionConfirm(memberUUID);
        FilesEntity filesEntity = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getContributionFile();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(filesEntity.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                .body(filesEntity.getData());

    }

    @GetMapping("/downloadPersonalCard/{memberUUID}")
    public ResponseEntity<byte[]> getPersonalCardFile(@PathVariable UUID memberUUID) throws IOException, DocumentException {
        filesService.personalCardFile(memberUUID);
        FilesEntity filesEntity = memberRepository.findById(memberUUID).get().getPersonalCardFile();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(filesEntity.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                .body(filesEntity.getData());

    }

    @GetMapping("/downloadAmmunitionList/{ammoListUUID}")
    public ResponseEntity<byte[]> getAmmoListFile(@PathVariable UUID ammoListUUID) {
        FilesEntity filesEntity = ammoEvidenceRepository.findById(ammoListUUID).get().getFile();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(filesEntity.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + filesEntity.getName() + "\"")
                .body(filesEntity.getData());
    }

}
