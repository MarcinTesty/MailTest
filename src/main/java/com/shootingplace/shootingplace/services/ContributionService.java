package com.shootingplace.shootingplace.services;


import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Contribution;
import com.shootingplace.shootingplace.repositories.ContributionRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;
    private final HistoryService historyService;
    private final FilesService filesService;
    private final Logger LOG = LogManager.getLogger(getClass());


    public ContributionService(ContributionRepository contributionRepository, MemberRepository memberRepository, HistoryService historyService, FilesService filesService) {
        this.contributionRepository = contributionRepository;
        this.memberRepository = memberRepository;
        this.historyService = historyService;
        this.filesService = filesService;
    }

    public void addContribution(UUID memberUUID, Contribution contribution) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getContribution() != null) {
            LOG.error("nie można już dodać pola ze składką");
        }
        ContributionEntity contributionEntity = Mapping.map(contribution);
        contributionRepository.saveAndFlush(contributionEntity);
        memberEntity.setContribution(contributionEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Składka została zapisana");
    }

    public void updateContribution(UUID memberUUID, Contribution contribution) {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            ContributionEntity contributionEntity = contributionRepository.findById(memberEntity
                    .getContribution()
                    .getUuid())
                    .orElseThrow(EntityNotFoundException::new);
            if (contribution.getContribution() != null) {
                LocalDate localDate = contribution.getContribution();
                if (localDate.isBefore(LocalDate.of(contribution.getContribution().getYear(), 6, 30))) {
                    localDate = LocalDate.of(contribution.getContribution().getYear(), 6, 30);
                } else {
                    localDate = LocalDate.of(contribution.getContribution().getYear(), 12, 31);
                }
                contributionEntity.setPaymentDay(LocalDate.now());
                contributionEntity.setContribution(localDate);
                LOG.info("Składka przedłużona do : " + contributionEntity.getContribution());
                LOG.info("składka opłacona dnia : " + contributionEntity.getPaymentDay());
            }
            contributionEntity.setPaymentDay(LocalDate.now());
            contributionRepository.saveAndFlush(contributionEntity);
            memberRepository.saveAndFlush(memberEntity);
            LOG.info("zaktualizowano składkę");

    }

    public boolean prolongContribution(UUID memberUUID) {

        if (memberRepository.existsById(memberUUID)) {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

            LocalDate prolong = memberEntity.getContribution().getContribution().plusMonths(6);
            if (prolong.getMonth().getValue() == 12) {
                prolong = prolong.plusDays(1);
            }
            if (memberEntity.getContribution().getContribution().isAfter(LocalDate.of(LocalDate.now().getYear(), 9, 30)) |
                    memberEntity.getContribution().getContribution().isAfter(LocalDate.of(LocalDate.now().getYear(), 3, 31))) {
                LOG.info("klubowicz jest już aktywny");
                memberEntity.setActive(true);
            } else {
                memberEntity.setActive(false);
            }
            ContributionEntity contributionEntity = contributionRepository.findById(memberEntity
                    .getContribution()
                    .getUuid())
                    .orElseThrow(EntityNotFoundException::new);
            contributionEntity.setContribution(prolong);
            contributionEntity.setPaymentDay(LocalDate.now());

            historyService.addContributionRecord(memberEntity.getUuid());
            contributionRepository.saveAndFlush(contributionEntity);
            memberEntity.setContribution(contributionEntity);
            memberRepository.saveAndFlush(memberEntity);
            try {
                filesService.contributionConfirm(memberUUID);
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
            LOG.info("Składka została przedłużona do : " + contributionEntity.getContribution());
            LOG.info("Składka została przedłużona dnia : " + contributionEntity.getPaymentDay());
            return true;
        } else
            LOG.error("Nie znaleziono takiego klubowicza");
        return false;
    }

//    private boolean contributionConfirmation(LocalDate prolongTo, MemberEntity member) {
//        System.out.println("Drukowanie potwierdzenia składki");
//        Document document = new Document(PageSize.A5);
//        try {
//            PdfWriter.getInstance(document,
//                    new FileOutputStream("C:\\Users\\izebr\\Desktop\\Składki\\Składka_"
//                            + member.getFirstName()
//                            + "_"
//                            + member.getSecondName()
//                            + "_"
//                            + LocalDate.now()
//                            + ".pdf"));
//            document.open();
//            document.addTitle("Składka_" + member.getFirstName() + "_" + member.getSecondName() + "_" + LocalDate.now());
//            document.addCreationDate();
//
//
//            Paragraph p = new Paragraph();
//
//            Paragraph p = new Paragraph(Element.ALIGN_JUSTIFIED);
//            p.add(new Chunk("Potwierdzenie opłacenia składki członkowskiej"
//                    , new Font(Font.FontFamily.TIMES_ROMAN, 20)));
//            p.add(new Chunk(getSex(member.getPesel()) + " " + member.getFirstName() + " " + member.getSecondName()
//                    , new Font(Font.FontFamily.TIMES_ROMAN, 20)));
//            p.add(new Chunk("Składka opłacona do : " + prolongTo, new Font(Font.FontFamily.TIMES_ROMAN, 20)));
//            document.add(new Paragraph(p));
//            document.close();
//            return true;
//        } catch (DocumentException | FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//
//    }
//
//    private String getSex(String pesel) {
//        int i = (int) pesel.charAt(8);
//        if (i % 2 == 1) {
//            return "Pan";
//        } else return "Pani";
//    }

}
