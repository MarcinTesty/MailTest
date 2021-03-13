package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.domain.models.MemberDTO;
import com.shootingplace.shootingplace.services.ChangeHistoryService;
import com.shootingplace.shootingplace.services.HistoryService;
import com.shootingplace.shootingplace.services.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/license")
@CrossOrigin
public class LicenseController {

    private final ChangeHistoryService changeHistoryService;
    private final LicenseService licenseService;
    private final HistoryService historyService;

    public LicenseController(ChangeHistoryService changeHistoryService, LicenseService licenseService, HistoryService historyService) {
        this.changeHistoryService = changeHistoryService;
        this.licenseService = licenseService;
        this.historyService = historyService;
    }

    @GetMapping("/membersWithValidLicense")
    public ResponseEntity<List<MemberDTO>> getMembersNamesAndLicense() {
        return ResponseEntity.ok(licenseService.getMembersNamesAndLicense());
    }

    @GetMapping("/membersWithNotValidLicense")
    public ResponseEntity<List<MemberDTO>> getMembersNamesAndLicenseNotValid() {
        return ResponseEntity.ok(licenseService.getMembersNamesAndLicenseNotValid());
    }

    @GetMapping("/membersQuantity")
    public List<Long> getMembersQuantity() {
        return licenseService.getMembersQuantity();
    }

    @PutMapping("/{memberUUID}")
    public ResponseEntity<?> updateLicense(@PathVariable String memberUUID, @RequestBody License license) {
        if (licenseService.updateLicense(memberUUID, license)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/forceUpdate")
    public ResponseEntity<?> updateLicense(@RequestParam String memberUUID, @RequestParam String number, @RequestParam String date, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {
            LocalDate parseDate = null;
            if (date != null && !date.isEmpty() && !date.equals("null")) {
                parseDate = LocalDate.parse(date);
            }
            if (licenseService.updateLicense(memberUUID, number, parseDate, pinCode)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(403).body("Brak dostępu");
        }
    }

    @PatchMapping("/{memberUUID}")
    public ResponseEntity<?> renewLicenseValidDate(@PathVariable String memberUUID, @RequestBody License license) {
        if (licenseService.renewLicenseValid(memberUUID, license)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/history/{memberUUID}")
    public ResponseEntity<?> addLicensePaymentHistory(@PathVariable String memberUUID) {
        if (historyService.addLicenseHistoryPayment(memberUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
