package com.shootingplace.shootingplace.services;


import com.itextpdf.text.*;

import com.itextpdf.text.pdf.PdfWriter;
import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Contribution;
import com.shootingplace.shootingplace.repositories.ContributionRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;

    public ContributionService(ContributionRepository contributionRepository, MemberRepository memberRepository) {
        this.contributionRepository = contributionRepository;
        this.memberRepository = memberRepository;
    }

    public boolean addContribution(UUID memberUUID, Contribution contribution) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getContribution() != null) {
            System.out.println("nie można już dodać pola ze składką");
            return false;
        }
        ContributionEntity contributionEntity = Mapping.map(contribution);
        contributionRepository.saveAndFlush(contributionEntity);
        memberEntity.setContribution(contributionEntity);
        memberRepository.saveAndFlush(memberEntity);
        System.out.println("Składka została zapisana");
        return true;
    }

    public boolean updateContribution(UUID memberUUID, Contribution contribution) {
        try {
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
                contributionEntity.setContribution(localDate);
                System.out.println("Składka przedłużona do : " + contributionEntity.getContribution());
            }
            contributionRepository.saveAndFlush(contributionEntity);
            memberRepository.saveAndFlush(memberEntity);
            System.out.println("zaktualizowano składkę");
            return true;
        } catch (Exception ex) {
            ex.getMessage();
            return false;
        }
    }

    public boolean prolongContribution(UUID memberUUID, Integer contributionAmount) {

        if (memberRepository.existsById(memberUUID)) {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            if (memberEntity.getActive().equals(false)) {
                System.out.println("Klubowicz nie aktywny");
                return false;
            } else {
                if(contributionAmount>2){
                    System.out.println("nie można przedłużyć o więcej niż 2 składki");
                    return false;}
                LocalDate prolong = memberEntity.getContribution().getContribution().plusMonths(6 * contributionAmount);
                ContributionEntity contributionEntity = contributionRepository.findById(memberEntity
                        .getContribution()
                        .getUuid())
                        .orElseThrow(EntityNotFoundException::new);
                contributionEntity.setContribution(prolong);
                contributionRepository.saveAndFlush(contributionEntity);
                memberEntity.setContribution(contributionEntity);
                memberRepository.saveAndFlush(memberEntity);
//                contributionConfirmation(contributionEntity.getContribution(), memberEntity);
                System.out.println("Składka zostałą przedłużona do : " + contributionEntity.getContribution());
            }
            return true;
        } else
            System.out.println("Nie znaleziono takiego klubowicza");
        return false;
    }

    private boolean contributionConfirmation(LocalDate prolongTo, MemberEntity member) {
        System.out.println("Drukowanie potwierdzenia składki");
        Document document = new Document(PageSize.A5);
        try {
            PdfWriter.getInstance(document,
                    new FileOutputStream("C:\\Users\\izebr\\Desktop\\Składki\\Składka_"
                            + member.getFirstName()
                            + "_"
                            + member.getSecondName()
                            + "_"
                            + LocalDate.now()
                            + ".pdf"));
            document.open();
            document.addTitle("Składka_" + member.getFirstName() + "_" + member.getSecondName() + "_" + LocalDate.now());
            document.addCreationDate();


            Paragraph p = new Paragraph();

//
//            Paragraph p = new Paragraph(Element.ALIGN_JUSTIFIED);
//            p.add(new Chunk("Potwerdzenie opłacenia składki członkowskiej"
//                    , new Font(Font.FontFamily.TIMES_ROMAN, 20)));
//            p.add(new Chunk(getSex(member.getPesel()) + " " + member.getFirstName() + " " + member.getSecondName()
//                    , new Font(Font.FontFamily.TIMES_ROMAN, 20)));
//            p.add(new Chunk("Składka opłacona do : " + prolongTo, new Font(Font.FontFamily.TIMES_ROMAN, 20)));
//            document.add(new Paragraph(p));
            document.close();
            return true;
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }


    }

    private String getSex(String pesel) {
        int i = (int) pesel.charAt(8);
        if (i % 2 == 1) {
            return "Pan";
        } else return "Pani";
    }

}
