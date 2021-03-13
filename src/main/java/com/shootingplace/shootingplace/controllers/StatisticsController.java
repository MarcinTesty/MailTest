package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.ContributionService;
import com.shootingplace.shootingplace.services.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
@CrossOrigin
public class StatisticsController {

    private final ContributionService contributionService;
    private final StatisticsService statisticsService;

    public StatisticsController(ContributionService contributionService, StatisticsService statisticsService) {
        this.contributionService = contributionService;
        this.statisticsService = statisticsService;
    }


    @GetMapping("/contributionSum")
    public ResponseEntity<?> getContributionSum(@RequestParam String firstDate, @RequestParam String secondDate, @RequestParam boolean condition) {
        LocalDate parseFirstDate = LocalDate.parse(firstDate);
        LocalDate parseSecondDate = LocalDate.parse(secondDate);
        return ResponseEntity.ok(contributionService.getContributionSum(parseFirstDate, parseSecondDate,condition));
    }

    @GetMapping("/joinDateSum")
    public ResponseEntity<?> getJoinDateSum(@RequestParam String firstDate, @RequestParam String secondDate) {
        LocalDate parseFirstDate = LocalDate.parse(firstDate);
        LocalDate parseSecondDate = LocalDate.parse(secondDate);
        return ResponseEntity.ok(contributionService.getJoinDateSum(parseFirstDate, parseSecondDate));
    }

    @GetMapping("/erasedSum")
    public ResponseEntity<?> getErasedMembersSum(@RequestParam String firstDate, @RequestParam String secondDate) {
        LocalDate parseFirstDate = LocalDate.parse(firstDate);
        LocalDate parseSecondDate = LocalDate.parse(secondDate);
        return ResponseEntity.ok(contributionService.getErasedMembersSum(parseFirstDate, parseSecondDate));
    }

    @GetMapping("/licenseSum")
    public ResponseEntity<?> getLicenseSum(@RequestParam String firstDate, @RequestParam String secondDate) {
        LocalDate parseFirstDate = LocalDate.parse(firstDate);
        LocalDate parseSecondDate = LocalDate.parse(secondDate);
        return ResponseEntity.ok(contributionService.getLicenseSum(parseFirstDate, parseSecondDate));
    }

    @GetMapping("/joinMonthSum")
    public ResponseEntity<?> joinMonthSum(@RequestParam String year) {
        LocalDate parseYear = LocalDate.parse(year);
        return ResponseEntity.ok(contributionService.joinMonthSum(parseYear.getYear()));
    }
    @GetMapping("/maxLegNumber")
    public ResponseEntity<?> getMaxLegNumber(){
        return ResponseEntity.ok(statisticsService.getMaxLegNumber());
    }

    @GetMapping("/actualYearMemberCounts")
    public ResponseEntity<?> getActualYearMemberCounts() {
        return ResponseEntity.ok(statisticsService.getActualYearMemberCounts());
    }
}
