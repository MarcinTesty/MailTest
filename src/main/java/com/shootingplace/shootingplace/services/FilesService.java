package com.shootingplace.shootingplace.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.models.FilesModel;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import com.shootingplace.shootingplace.repositories.FilesRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.TournamentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilesService {
    private static BaseFont czcionka;


    private final MemberRepository memberRepository;
    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final FilesRepository filesRepository;
    private final TournamentRepository tournamentRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public FilesService(MemberRepository memberRepository, AmmoEvidenceRepository ammoEvidenceRepository, FilesRepository filesRepository, TournamentRepository tournamentRepository) {
        this.memberRepository = memberRepository;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
        this.filesRepository = filesRepository;
        this.tournamentRepository = tournamentRepository;
    }

    private FilesEntity createContributionFileEntity(FilesModel filesModel) {
        FilesEntity filesEntity = Mapping.map(filesModel);
        LOG.info("Potwierdzenia Składki zostało zapisane");
        return filesRepository.saveAndFlush(filesEntity);

    }

    private FilesEntity createPersonalCardFileEntity(FilesModel filesModel) {
        FilesEntity filesEntity = Mapping.map(filesModel);
        LOG.info("Karta Personalnej została zapisana");
        return filesRepository.saveAndFlush(filesEntity);

    }

    private FilesEntity createAmmoListFileEntity(FilesModel filesModel) {

        FilesEntity filesEntity = Mapping.map(filesModel);
        LOG.info("Lista Amunicji została zapisane");
        return filesRepository.saveAndFlush(filesEntity);

    }

    private FilesEntity createApplicationForExtensionOfTheCompetitorsLicense(FilesModel filesModel) {

        FilesEntity filesEntity = Mapping.map(filesModel);
        LOG.info("Wniosek został zapisany");
        return filesRepository.saveAndFlush(filesEntity);

    }

    private FilesEntity createAnnouncementFromCompetition(FilesModel filesModel) {

        FilesEntity filesEntity = Mapping.map(filesModel);
        LOG.info("Komunikat został zapisany");
        return filesRepository.saveAndFlush(filesEntity);

    }


    public FilesEntity contributionConfirm(String memberUUID) throws DocumentException, IOException {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LocalDate contribution = memberEntity.getHistory().getContributionList().get(0).getPaymentDay();
        LocalDate validThru = memberEntity.getHistory().getContributionList().get(0).getValidThru();
        String fileName = "Składka_" + memberEntity.getFirstName() + "_" + memberEntity.getSecondName() + "_" + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,
                new FileOutputStream(fileName));

        document.open();
        document.addTitle(fileName);
        document.addCreationDate();

        try {
            czcionka = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.CACHED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        String group;
        if (memberEntity.getAdult()) {
            group = "OGÓLNA";
        } else {
            group = "MŁODZIEŻOWA";
        }

        String status;
        if (getSex(memberEntity.getPesel()).equals("Pani")) {
            status = "opłaciła";
        } else {
            status = "opłacił";
        }
        String contributionLevel;
        if (memberEntity.getAdult()) {
            contributionLevel = "120";
        } else {
            contributionLevel = "50";
        }

        Paragraph p = new Paragraph(Element.ALIGN_JUSTIFIED, "KLUB STRZELECKI „DZIESIĄTKA” LOK W ŁODZI".toUpperCase() + "\n", new Font(czcionka, 14, Font.BOLD));
        Paragraph p1 = new Paragraph("Potwierdzenie opłacenia składki członkowskiej", new Font(czcionka, 14, Font.ITALIC));
        Paragraph h1 = new Paragraph("Grupa ", new Font(czcionka, 14));
        Phrase h2 = new Phrase(group, new Font(czcionka, 14, Font.BOLD));
        Paragraph p2 = new Paragraph("\n\nNazwisko i Imię : ", new Font(czcionka, 11));
        Phrase p3 = new Phrase(memberEntity.getSecondName() + " " + memberEntity.getFirstName(), new Font(czcionka, 18, Font.BOLD));
        Phrase p4 = new Phrase("Numer Legitymacji : ", new Font(czcionka, 11));
        Phrase p5 = new Phrase(String.valueOf(memberEntity.getLegitimationNumber()), new Font(czcionka, 18, Font.BOLD));
        Paragraph p6 = new Paragraph("\n\n\nData opłacenia składki : ", new Font(czcionka, 11));
        Phrase p7 = new Phrase(String.valueOf(contribution), new Font(czcionka, 11, Font.BOLD));
        Paragraph p8 = new Paragraph("\n\nSkładka ważna do : ", new Font(czcionka, 11));
        Phrase p9 = new Phrase(String.valueOf(validThru), new Font(czcionka, 11, Font.BOLD));
        Paragraph p10 = new Paragraph("\n\n\n" + getSex(memberEntity.getPesel()) + " ", new Font(czcionka, 11));
        Phrase p11 = new Phrase(memberEntity.getSecondName() + " " + memberEntity.getFirstName() + " dnia : " + contribution + " " + status + " półroczną składkę członkowską w wysokości " + contributionLevel + " PLN.", new Font(czcionka, 11));
        Paragraph p12 = new Paragraph("\n\n\n\n\nTermin opłacenia kolejnej składki : ", new Font(czcionka, 11));
        Paragraph p13 = new Paragraph("\n" + (validThru.plusMonths(3)), new Font(czcionka, 11, Font.BOLD));
        Paragraph p14 = new Paragraph("", new Font(czcionka, 11));
        Phrase p15 = new Phrase("\n\nSkładki uiszczane w trybie półrocznym muszą zostać opłacone najpóźniej do końca pierwszego " +
                "kwartału za pierwsze półrocze i analogicznie za drugie półrocze do końca trzeciego kwartału. W przypadku " +
                "niedotrzymania terminu wpłaty (raty), wysokość (raty) składki ulega powiększeniu o karę w wysokości 50%" +
                " zaległości. (Regulamin Opłacania Składek Członkowskich Klubu Strzeleckiego „Dziesiątka” LOK w Łodzi)", new Font(czcionka, 11, Font.ITALIC));
        Paragraph p16 = new Paragraph("\n\n\n\n\n\n\n\n\n", new Font(czcionka, 11));
        Paragraph p19 = new Paragraph("pieczęć klubu", new Font(czcionka, 11));
        Phrase p20 = new Phrase("                                                                 ");
        Phrase p21 = new Phrase("pieczęć i podpis osoby przyjmującej składkę");

        p.setIndentationLeft(100);
        p1.setIndentationLeft(120);
        h1.add(h2);
        h1.setIndentationLeft(190);
        p2.add(p3);
        p2.add("                                        ");
        p4.add(p5);
        p2.add(p4);
        p6.add(p7);
        p8.add(p9);
        p10.add(p11);
        p14.add(p15);

        p20.add(p21);
        p19.add(p20);
        p16.setIndentationLeft(25);
        p19.setIndentationLeft(40);


        document.add(p);
        document.add(p1);
        document.add(h1);
        document.add(p2);
        document.add(p6);
        document.add(p8);
        document.add(p10);
        document.add(p12);
        document.add(p13);
        document.add(p14);
        document.add(p16);
        document.add(p19);

        document.close();

        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createContributionFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    public FilesEntity personalCardFile(String memberUUID) throws IOException, DocumentException {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        String fileName = "Karta_Członkowska_" + memberEntity.getFirstName() + "_" + memberEntity.getSecondName() + ".pdf";
        LocalDate birthDate = birthDay(memberEntity.getPesel());
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,
                new FileOutputStream(fileName));

        document.open();
        document.addTitle(fileName);
        document.addCreationDate();

        String statement = "Oświadczenie:\n" +
                "- Zobowiązuję się do przestrzegania Regulaminu Strzelnicy, oraz Regulaminu Klubu Strzeleckiego „Dziesiątka” Ligi Obrony Kraju w Łodzi.\n" +
                "- Wyrażam zgodę na przesyłanie mi informacji przez Klub Strzelecki „Dziesiątka” za pomocą środków komunikacji elektronicznej, w szczególności pocztą elektroniczną oraz w postaci smsów/mms-ów.\n" +
                "Wyrażenie zgody jest dobrowolne i może być odwołane w każdym czasie w na podstawie oświadczenia skierowanego na adres siedziby Klubu, na podstawie oświadczenia przesłanego za pośrednictwem poczty elektronicznej na adres: biuro@ksdziesiatka.pl lub w inny uzgodniony sposób.\n" +
                "- Zgadzam się na przetwarzanie moich danych osobowych (w tym wizerunku) przez Administratora Danych, którym jest Stowarzyszenie Liga Obrony Kraju mające siedzibę główną w Warszawie pod adresem: \n" +
                "ul. Chocimska 14, 00-791 Warszawa w celach związanych z moim członkostwem w KS „Dziesiątka” LOK Łódź.";


        try {
            czcionka = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.CACHED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        Paragraph p = new Paragraph("KLUB STRZELECKI „DZIESIĄTKA” LOK W ŁODZI\n", new Font(czcionka, 14, Font.BOLD));
        p.setIndentationLeft(100);
        String group;
        if (memberEntity.getAdult()) {
            group = "OGÓLNA";
        } else {
            group = "MŁODZIEŻOWA";
        }
        Paragraph p1 = new Paragraph("Karta Członkowska\n", new Font(czcionka, 14, Font.ITALIC));
        Phrase p2 = new Phrase(group, new Font(czcionka, 14, Font.BOLD));
        Paragraph p3 = new Paragraph("\nNazwisko i Imię : ", new Font(czcionka, 11));
        Phrase p4 = new Phrase(memberEntity.getSecondName() + " " + memberEntity.getFirstName(), new Font(czcionka, 18, Font.BOLD));
        Phrase p5 = new Phrase("Numer Legitymacji : ", new Font(czcionka, 11));
        Phrase p6 = new Phrase(String.valueOf(memberEntity.getLegitimationNumber()), new Font(czcionka, 18, Font.BOLD));
        Paragraph p7 = new Paragraph("\nData Wstąpienia : ", new Font(czcionka, 11));
        Phrase p8 = new Phrase(String.valueOf(memberEntity.getJoinDate()), new Font(czcionka, 15));
        Paragraph p9 = new Paragraph("\nData Urodzenia : ", new Font(czcionka, 11));
        Phrase p10 = new Phrase(String.valueOf(birthDate));
        Paragraph p11 = new Paragraph("PESEL : " + memberEntity.getPesel(), new Font(czcionka, 11));
        Paragraph p12 = new Paragraph("", new Font(czcionka, 11));
        Phrase p13 = new Phrase(memberEntity.getIDCard());
        Paragraph p14 = new Paragraph("Telefon Kontaktowy : " + memberEntity.getPhoneNumber(), new Font(czcionka, 11));
        Paragraph p15 = new Paragraph("", new Font(czcionka, 11));
        Paragraph p16 = new Paragraph("\n\nAdres Zamieszkania", new Font(czcionka, 11));
        Paragraph p17 = new Paragraph("", new Font(czcionka, 11));
        Paragraph p18 = new Paragraph("\n\n" + statement, new Font(czcionka, 11));
        Paragraph p19 = new Paragraph("\n\n\n\n\n\n.............................................", new Font(czcionka, 11));
        Phrase p20 = new Phrase("                                                              ");
        Phrase p21 = new Phrase("............................................................");
        Paragraph p22 = new Paragraph("podpis przyjmującego", new Font(czcionka, 11));
        Phrase p23 = new Phrase("                                                                 ");
        Phrase p24 = new Phrase("miejscowość, data i podpis Klubowicza");

        p1.add("Grupa ");
        p1.add(p2);
        p1.add("\n");
        p1.setIndentationLeft(190);
        p3.add(p4);
        p3.add("                                        ");
        p5.add(p6);
        p3.add(p5);
        p7.add(p8);
        p7.add("\n");
        p9.add(p10);
        if (memberEntity.getAdult()) {
            p12.add("Numer Dowodu Osobistego : ");
        } else {
            p12.add("Numer Legitymacji Szkolnej / Numer Dowodu Osobistego : ");
        }
        p12.add(p13);
        p12.add("\n\n\n");
        if (memberEntity.getEmail() != null) {
            p15.add("Email : " + memberEntity.getEmail());
        } else {
            p15.add("Email : Nie podano");
        }
        if (memberEntity.getAddress().getPostOfficeCity() != null) {
            p17.add("Miasto : " + memberEntity.getAddress().getPostOfficeCity());
        } else {
            p17.add("Miasto : ");
        }
        p17.add("\n");
        if (memberEntity.getAddress().getZipCode() != null) {
            p17.add("Kod pocztowy : " + memberEntity.getAddress().getZipCode());
        } else {
            p17.add("Kod pocztowy : ");
        }
        p17.add("\n");
        if (memberEntity.getAddress().getStreet() != null) {
            p17.add("Ulica : " + memberEntity.getAddress().getStreet());
            if (memberEntity.getAddress().getStreetNumber() != null) {
                p17.add(" " + memberEntity.getAddress().getStreetNumber());
            } else {
                p17.add(" ");
            }
        } else {
            p17.add("Ulica : ");
        }
        p17.add("\n");
        if (memberEntity.getAddress().getFlatNumber() != null) {
            p17.add("Numer Mieszkania : " + memberEntity.getAddress().getFlatNumber());
        } else {
            p17.add("Numer Mieszkania : ");
        }
        p20.add(p21);
        p19.add(p20);
        p19.setIndentationLeft(25);
        p22.setIndentationLeft(40);
        p22.add(p23);
        p22.add(p24);

        document.add(p);
        document.add(p1);
        document.add(p3);
        document.add(p7);
        document.add(p9);
        document.add(p11);
        document.add(p12);
        document.add(p14);
        document.add(p15);
        document.add(p16);
        document.add(p17);
        document.add(p18);
        document.add(p19);
        document.add(p22);


        document.close();

        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createPersonalCardFileEntity(filesModel);


        filesEntity.setName(fileName);
        filesEntity.setType(String.valueOf(MediaType.APPLICATION_PDF));
        filesEntity.setData(data);
        File file = new File(fileName);

        file.delete();
        return filesEntity;

    }

    public FilesEntity createAmmunitionListDocument(String ammoEvidenceUUID) throws IOException, DocumentException {
        AmmoEvidenceEntity ammoEvidenceEntity = ammoEvidenceRepository.findById(ammoEvidenceUUID).orElseThrow(EntityNotFoundException::new);
        List<AmmoInEvidenceEntity> ammoInEvidenceEntityList = ammoEvidenceEntity.getAmmoInEvidenceEntityList();

        String fileName = "Lista_Amunicyjna_" + ammoEvidenceEntity.getDate() + ".pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        writer.setPageEvent(new PageStamper());
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();

        try {
            czcionka = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.CACHED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }


        Paragraph number = new Paragraph(ammoEvidenceEntity.getNumber(), font(10, 4));
        Paragraph p = new Paragraph("KLUB STRZELECKI „DZIESIĄTKA” LOK W ŁODZI\n", new Font(czcionka, 14, Font.BOLD));
        Paragraph p1 = new Paragraph("Lista rozliczenia amunicji " + ammoEvidenceEntity.getDate(), new Font(czcionka, 12, Font.ITALIC));

        number.setIndentationLeft(450);
        p.setIndentationLeft(100);
        p1.add("\n");
        p1.setIndentationLeft(190);

        document.add(number);
        document.add(p);
        document.add(p1);
        for (AmmoInEvidenceEntity ammoInEvidenceEntity : ammoInEvidenceEntityList) {
            Paragraph p2 = new Paragraph("Kaliber : " + ammoInEvidenceEntity.getCaliberName() + "\n", font(12, 1));
            p2.add("\n");
            p2.setIndentationLeft(230);
            document.add(p2);
            float[] pointColumnWidths = {20F, 255F, 25};
            PdfPTable tableLabel = new PdfPTable(pointColumnWidths);
            PdfPCell cellLabel = new PdfPCell(new Paragraph(new Paragraph("lp.", new Font(czcionka, 10, Font.ITALIC))));
            PdfPCell cell1Label = new PdfPCell(new Paragraph(new Paragraph("Imię i Nazwisko", new Font(czcionka, 10, Font.ITALIC))));
            PdfPCell cell2Label = new PdfPCell(new Paragraph(new Paragraph("ilość sztuk", new Font(czcionka, 10, Font.ITALIC))));


            tableLabel.addCell(cellLabel);
            tableLabel.addCell(cell1Label);
            tableLabel.addCell(cell2Label);
            document.add(tableLabel);
            for (int j = 0; j < ammoInEvidenceEntity.getAmmoUsedToEvidenceEntityList().size(); j++) {
                PdfPTable table = new PdfPTable(pointColumnWidths);
                PdfPCell cell;
                PdfPCell cell1;
                PdfPCell cell2;

                String name;
                if (ammoInEvidenceEntity.getAmmoUsedToEvidenceEntityList().get(j).getMemberEntity() == null) {
                    OtherPersonEntity otherPersonEntity = ammoInEvidenceEntity.getAmmoUsedToEvidenceEntityList().get(j).getOtherPersonEntity();
                    name = otherPersonEntity.getSecondName() + " " + otherPersonEntity.getFirstName();
                } else {
                    MemberEntity memberEntity = ammoInEvidenceEntity.getAmmoUsedToEvidenceEntityList().get(j).getMemberEntity();
                    name = memberEntity.getSecondName() + " " + memberEntity.getFirstName();
                }

                cell = new PdfPCell(new Paragraph(String.valueOf(j + 1), new Font(czcionka, 10, Font.ITALIC)));
                cell1 = new PdfPCell(new Paragraph(name, new Font(czcionka, 10, Font.ITALIC)));
                cell2 = new PdfPCell(new Paragraph(ammoInEvidenceEntity.getAmmoUsedToEvidenceEntityList().get(j).getCounter().toString(), new Font(czcionka, 10, Font.ITALIC)));
                table.addCell(cell);
                table.addCell(cell1);
                table.addCell(cell2);
                document.add(table);
            }
            PdfPTable tableSum = new PdfPTable(pointColumnWidths);
            PdfPCell cellTableSum = new PdfPCell(new Paragraph(new Paragraph("", new Font(czcionka, 10, Font.ITALIC))));
            PdfPCell cellTableSum1 = new PdfPCell(new Paragraph(new Paragraph("Suma", new Font(czcionka, 10, Font.ITALIC))));
            PdfPCell cellTableSum2 = new PdfPCell(new Paragraph(new Paragraph(ammoInEvidenceEntity.getQuantity().toString(), new Font(czcionka, 10, Font.ITALIC))));
            cellTableSum.setBorder(0);
            cellTableSum1.setBorder(0);
            cellTableSum1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

            tableSum.addCell(cellTableSum);
            tableSum.addCell(cellTableSum1);
            tableSum.addCell(cellTableSum2);
            document.add(tableSum);

        }

        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createAmmoListFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;

    }

    public FilesEntity createApplicationForExtensionOfTheCompetitorsLicense(String memberUUID) throws IOException, DocumentException {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        String fileName = "Wniosek_" + memberEntity.getFirstName() + " " + memberEntity.getSecondName() + ".pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        PdfContentByte cb = writer.getDirectContent();

        PdfReader reader = new PdfReader("C:\\Users\\izebr\\IdeaProjects\\shootingplace\\src\\main\\resources\\Wniosek_o_przedluzenie_licencji_zawodniczej.pdf");
        PdfImportedPage page = writer.getImportedPage(reader, 1);

        document.newPage();
        cb.addTemplate(page, 0, 0);

        String licenseNumber = memberEntity.getLicense().getNumber();
        char[] P = memberEntity.getPesel().toCharArray();
        String phone = memberEntity.getPhoneNumber();
        String name = memberEntity.getSecondName().toUpperCase() + "  " + memberEntity.getFirstName().toUpperCase();
        String splited = phone.substring(0, 3) + " ";
        String splited1 = phone.substring(3, 6) + " ";
        String splited2 = phone.substring(6, 9) + " ";
        String splited3 = phone.substring(9, 12) + " ";
        String phoneSplited = splited + splited1 + splited2 + splited3;
        String date = memberEntity.getLicense().getValidThru().toString().substring(2, 4);

        // brać uprawnienia  z patentu
        int pistol = 0;
        if (memberEntity.getShootingPatent().getPistolPermission()) {

            pistol = memberEntity.getHistory().getPistolCounter();
        }
        int rifle = 0;
        if (memberEntity.getShootingPatent().getRiflePermission()) {

            rifle = memberEntity.getHistory().getRifleCounter();
        }
        int shotgun = 0;
        if (memberEntity.getShootingPatent().getShotgunPermission()) {

            shotgun = memberEntity.getHistory().getShotgunCounter();
        }

        if (pistol >= 4) {
            if (rifle > 2) {
                rifle = 2;
            }
            if (shotgun > 2) {
                shotgun = 2;
            }
            pistol = 4;

        } else if (rifle >= 4) {
            if (pistol > 2) {
                pistol = 2;
            }
            if (shotgun > 2) {
                shotgun = 2;
            }
            rifle = 4;

        } else if (shotgun >= 4) {
            if (pistol > 2) {
                pistol = 2;
            }
            if (rifle > 2) {
                rifle = 2;
            }
            shotgun = 4;

        }
        LocalDate validThru = memberEntity.getLicense().getValidThru();

        List<CompetitionHistoryEntity> competitions = memberEntity
                .getHistory()
                .getCompetitionHistory()
                .stream()
                .filter(f -> f.getDate().isBefore(validThru))
//                .filter(f -> f.getDate().isAfter(LocalDate.of(validThru.getYear(), 1, 1)))
                .collect(Collectors.toList());
        String pistolet = "Pistolet";
        String karabin = "Karabin";
        String strzelba = "Strzelba";
        // podzielić na dyscypliny
        List<CompetitionHistoryEntity> pistolCollect = competitions.
                stream()
                .filter(f -> f.getDiscipline()
                        .contains(pistolet))
                .sorted(Comparator.comparing(CompetitionHistoryEntity::getDate)
                        .reversed()).collect(Collectors.toList());

        List<CompetitionHistoryEntity> karabinCollect = competitions
                .stream()
                .filter(f -> f.getDiscipline()
                        .contains(karabin))
                .sorted(Comparator.comparing(CompetitionHistoryEntity::getDate)
                        .reversed()).collect(Collectors.toList());

        List<CompetitionHistoryEntity> strzelbaCollect = competitions
                .stream()
                .filter(f -> f.getDiscipline()
                        .contains(strzelba))
                .sorted(Comparator.comparing(CompetitionHistoryEntity::getDate)
                        .reversed()).collect(Collectors.toList());

        // zapisać tylko tyle ile potrzeba


        Paragraph patentNumber = new Paragraph(memberEntity.getShootingPatent().getPatentNumber() + "                                                       " + licenseNumber, font(12, 0));

        patentNumber.setIndentationLeft(160);

        Paragraph newLine = new Paragraph("\n", font(7, 0));
        Paragraph pesel = new Paragraph(P[0] + "   " + P[1] + "   " + P[2] + "   " + P[3] + "   " + P[4] + "   " + P[5] + "  " + P[6] + "   " + P[7] + "   " + P[8] + "   " + P[9] + "   " + P[10] + "                                             " + phoneSplited, font(12, 0));
        Paragraph names = new Paragraph(name, font(12, 0));
        Paragraph year = new Paragraph(date, font(12, 1));
        pesel.setIndentationLeft(72);
        names.setIndentationLeft(150);
        year.setIndentationLeft(350);

        for (int i = 0; i < 12; i++) {

            document.add(newLine);
        }

        document.add(patentNumber);

        for (int i = 0; i < 1; i++) {

            document.add(newLine);
        }
        document.add(pesel);
        document.add(new Paragraph("\n", font(4, 0)));
        for (int i = 0; i < 1; i++) {

            document.add(newLine);
        }
        document.add(names);
        for (int i = 0; i < 5; i++) {

            document.add(newLine);
        }
        document.add(year);

        for (int i = 0; i < 3; i++) {

            document.add(newLine);
        }
        for (int i = 0; i < pistol; i++) {
            float[] pointColumnWidths = {50, 20, 20, 5, 10, 2, 28};
            PdfPTable table = new PdfPTable(pointColumnWidths);


            PdfPCell cell = new PdfPCell(new Paragraph(pistolCollect.get(i).getName(), font(9, 0)));
            PdfPCell cell1 = new PdfPCell(new Paragraph(" " + pistolCollect.get(i).getDate().toString(), font(9, 0)));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Łódź", font(9, 0)));
            PdfPCell cell3 = new PdfPCell(new Paragraph("X", font(9, 1)));
            PdfPCell cell4 = new PdfPCell(new Paragraph(" ", font(9, 0)));
            PdfPCell cell5 = new PdfPCell(new Paragraph(" ", font(9, 0)));
            PdfPCell cell6 = new PdfPCell(new Paragraph("WZSS", font(9, 0)));
            cell.setBorder(0);
            cell1.setBorder(0);
            cell2.setBorder(0);
            cell3.setBorder(0);
            cell4.setBorder(0);
            cell5.setBorder(0);
            cell6.setBorder(0);
            cell.setUseDescender(true);
            table.addCell(cell);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            document.add(new Phrase("\n", font(5, 0)));
            document.add(table);
        }
        for (int i = 0; i < rifle; i++) {
            float[] pointColumnWidths = {50, 20, 20, 5, 2, 10, 28};
            PdfPTable table = new PdfPTable(pointColumnWidths);


            PdfPCell cell = new PdfPCell(new Paragraph(karabinCollect.get(i).getName(), font(9, 0)));
            PdfPCell cell1 = new PdfPCell(new Paragraph(" " + karabinCollect.get(i).getDate().toString(), font(9, 0)));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Łódź", font(9, 0)));
            PdfPCell cell3 = new PdfPCell(new Paragraph(" ", font(9, 0)));
            PdfPCell cell4 = new PdfPCell(new Paragraph("X", font(9, 1)));
            PdfPCell cell5 = new PdfPCell(new Paragraph(" ", font(9, 0)));
            PdfPCell cell6 = new PdfPCell(new Paragraph("WZSS", font(9, 0)));
            cell.setBorder(0);
            cell1.setBorder(0);
            cell2.setBorder(0);
            cell3.setBorder(0);
            cell4.setBorder(0);
            cell5.setBorder(0);
            cell6.setBorder(0);
            cell.setUseDescender(true);
            table.addCell(cell);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            document.add(new Phrase("\n", font(5, 0)));
            document.add(table);
        }
        for (int i = 0; i < shotgun; i++) {
            float[] pointColumnWidths = {50, 20, 20, 8, 3, 6, 28};
            PdfPTable table = new PdfPTable(pointColumnWidths);


            PdfPCell cell = new PdfPCell(new Paragraph(strzelbaCollect.get(i).getName(), font(9, 0)));
            PdfPCell cell1 = new PdfPCell(new Paragraph(" " + strzelbaCollect.get(i).getDate().toString(), font(9, 0)));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Łódź", font(9, 0)));
            PdfPCell cell3 = new PdfPCell(new Paragraph(" ", font(9, 0)));
            PdfPCell cell4 = new PdfPCell(new Paragraph(" ", font(9, 0)));
            PdfPCell cell5 = new PdfPCell(new Paragraph("X", font(9, 1)));
            PdfPCell cell6 = new PdfPCell(new Paragraph("WZSS", font(9, 0)));
            cell.setBorder(0);
            cell1.setBorder(0);
            cell2.setBorder(0);
            cell3.setBorder(0);
            cell4.setBorder(0);
            cell5.setBorder(0);
            cell6.setBorder(0);
            cell.setUseDescender(true);
            table.addCell(cell);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            document.add(new Phrase("\n", font(5, 0)));
            document.add(table);
        }
        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createApplicationForExtensionOfTheCompetitorsLicense(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;

    }

    public FilesEntity createAnnouncementFromCompetition(String tournamentUUID) throws IOException, DocumentException {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);


        String fileName = "Zawody_" + tournamentEntity.getName() + "_" + tournamentEntity.getDate() + ".pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        int page = 1;

        Paragraph title = new Paragraph(tournamentEntity.getName().toUpperCase() + "\n" + "KLUB STRZELECKI „DZIESIĄTKA” LOK W ŁODZI", font(13, 1));
        Paragraph date = new Paragraph("Łódź, " + dateFormat(tournamentEntity.getDate()), font(10, 2));
        Paragraph newLine = new Paragraph("\n", font(10, 0));

        document.add(title);
        document.add(date);
        document.add(newLine);

        PdfPTable mainTable = new PdfPTable(1);
        mainTable.setWidthPercentage(100);


        for (int i = 0; i < tournamentEntity.getCompetitionsList().size(); i++) {

            CompetitionMembersListEntity competitionMembersListEntity = tournamentEntity.getCompetitionsList().get(i);
            competitionMembersListEntity.getScoreList().sort(Comparator.comparing(ScoreEntity::getScore).reversed());

            Paragraph competition = new Paragraph(competitionMembersListEntity.getName(), font(14, 1));
            competition.add("\n");
            document.add(competition);
            float[] pointColumnWidths = {25F, 150F, 150F, 25F};
            PdfPTable tableLabel = new PdfPTable(pointColumnWidths);
            PdfPCell cellLabel = new PdfPCell(new Paragraph("M-ce", font(10, 0)));
            PdfPCell cellLabel1 = new PdfPCell(new Paragraph("Imię i Nazwisko", font(10, 0)));
            PdfPCell cellLabel2 = new PdfPCell(new Paragraph("Klub", font(10, 0)));
            PdfPCell cellLabel3 = new PdfPCell(new Paragraph("wynik", font(10, 0)));

            document.add(newLine);

            cellLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellLabel1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellLabel2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellLabel3.setHorizontalAlignment(Element.ALIGN_CENTER);

            cellLabel.setBorder(0);
            cellLabel1.setBorder(0);
            cellLabel2.setBorder(0);
            cellLabel3.setBorder(0);

            tableLabel.setWidthPercentage(100F);
            tableLabel.addCell(cellLabel);
            tableLabel.addCell(cellLabel1);
            tableLabel.addCell(cellLabel2);
            tableLabel.addCell(cellLabel3);

            document.add(tableLabel);


            for (int j = 0; j < competitionMembersListEntity.getScoreList().size(); j++) {

                String secondName;
                String firstName;
                String club;
                if (competitionMembersListEntity.getScoreList().get(j).getMember() != null) {
                    secondName = competitionMembersListEntity.getScoreList().get(j).getMember().getSecondName();
                    firstName = competitionMembersListEntity.getScoreList().get(j).getMember().getFirstName();
                    club = competitionMembersListEntity.getScoreList().get(j).getMember().getClub().getName();

                } else {
                    secondName = competitionMembersListEntity.getScoreList().get(j).getOtherPersonEntity().getSecondName();
                    firstName = competitionMembersListEntity.getScoreList().get(j).getOtherPersonEntity().getFirstName();
                    club = competitionMembersListEntity.getScoreList().get(j).getOtherPersonEntity().getClub().getName();

                }
                float score = competitionMembersListEntity.getScoreList().get(j).getScore();
                PdfPTable playerTableLabel = new PdfPTable(pointColumnWidths);
                PdfPCell playerCellLabel = new PdfPCell(new Paragraph(String.valueOf(j + 1), font(11, 0)));
                PdfPCell playerCellLabel1 = new PdfPCell(new Paragraph(secondName + " " + firstName, font(11, 0)));
                PdfPCell playerCellLabel2 = new PdfPCell(new Paragraph(club, font(11, 0)));
                PdfPCell playerCellLabel3 = new PdfPCell(new Paragraph(String.valueOf(score).replace(".0", ""), font(11, 0)));

                playerCellLabel.setBorder(0);
                playerCellLabel1.setBorder(0);
                playerCellLabel2.setBorder(0);
                playerCellLabel3.setBorder(0);

                playerCellLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                playerCellLabel1.setHorizontalAlignment(Element.ALIGN_CENTER);
                playerCellLabel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                playerCellLabel3.setHorizontalAlignment(Element.ALIGN_CENTER);


                playerTableLabel.setWidthPercentage(100F);

                playerTableLabel.addCell(playerCellLabel);
                playerTableLabel.addCell(playerCellLabel1);
                playerTableLabel.addCell(playerCellLabel2);
                playerTableLabel.addCell(playerCellLabel3);

                document.add(playerTableLabel);

            }

        }
        float[] pointColumnWidths = {220F, 240F, 240F};
        String mainArbiter;
        String mainArbiterClass;
        if (tournamentEntity.getMainArbiter() != null) {
            mainArbiter = tournamentEntity.getMainArbiter().getFirstName() + " " + tournamentEntity.getMainArbiter().getSecondName();
            mainArbiterClass = tournamentEntity.getMainArbiter().getMemberPermissions().getArbiterClass();
        } else {
            mainArbiter = tournamentEntity.getOtherMainArbiter().getFirstName() + " " + tournamentEntity.getOtherMainArbiter().getSecondName();
            mainArbiterClass = tournamentEntity.getOtherMainArbiter().getPermissionsEntity().getArbiterClass();
        }

        String arbiterRTS = "";
        String arbiterRTSClass = "";
        if (tournamentEntity.getCommissionRTSArbiter() != null && tournamentEntity.getOtherCommissionRTSArbiter() != null) {
            if (tournamentEntity.getCommissionRTSArbiter() != null) {
                arbiterRTS = tournamentEntity.getCommissionRTSArbiter().getFirstName() + " " + tournamentEntity.getCommissionRTSArbiter().getSecondName();
                arbiterRTSClass = tournamentEntity.getCommissionRTSArbiter().getMemberPermissions().getArbiterClass();
            } else {
                arbiterRTS = tournamentEntity.getOtherCommissionRTSArbiter().getFirstName() + " " + tournamentEntity.getOtherCommissionRTSArbiter().getSecondName();
                arbiterRTSClass = tournamentEntity.getOtherCommissionRTSArbiter().getPermissionsEntity().getArbiterClass();
            }
        }


        PdfPTable arbiterTableLabel = new PdfPTable(pointColumnWidths);
        PdfPCell arbiterCellLabel1 = new PdfPCell(new Paragraph("Sędzia Główny \n" + mainArbiter + "\n" + mainArbiterClass, font(12, 0)));
        PdfPCell arbiterCellLabel2 = new PdfPCell(new Paragraph(" ", font(10, 0)));
        PdfPCell arbiterCellLabel3 = new PdfPCell(new Paragraph("Przewodniczący Komisji RTS \n" + arbiterRTS + "\n" + arbiterRTSClass, font(12, 0)));

        arbiterCellLabel1.setHorizontalAlignment(Element.ALIGN_CENTER);
        arbiterCellLabel2.setHorizontalAlignment(Element.ALIGN_CENTER);
        arbiterCellLabel3.setHorizontalAlignment(Element.ALIGN_CENTER);

        arbiterCellLabel1.setBorder(0);
        arbiterCellLabel2.setBorder(0);
        arbiterCellLabel3.setBorder(0);

        arbiterTableLabel.setWidthPercentage(100F);

        arbiterTableLabel.addCell(arbiterCellLabel1);
        arbiterTableLabel.addCell(arbiterCellLabel2);
        arbiterTableLabel.addCell(arbiterCellLabel3);


        document.add(newLine);
        document.add(newLine);

        document.add(arbiterTableLabel);

        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createAnnouncementFromCompetition(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    private String getSex(String pesel) {
        int i = (int) pesel.charAt(8);
        if (i % 2 == 1) {
            return "Pani";
        } else return "Pan";
    }

    private byte[] convertToByteArray(String path) throws IOException {
        File file = new File(path);
        return Files.readAllBytes(file.toPath());

    }

    private LocalDate birthDay(String pesel) {

        int year = Integer.parseInt(pesel.substring(0, 2));
        year += (year < 5) ? 2000 : 1900;
        int month = Integer.parseInt(pesel.substring(2, 4));
        if (month > 12) {
            month -= 20;
        }
        int day = Integer.parseInt(pesel.substring(4, 6));

        return LocalDate.of(year, month, day);
    }

    public void delete(FilesEntity filesEntity) {
        filesRepository.delete(filesEntity);

    }

    private Font font(int size, int style) throws IOException, DocumentException {
//        1 - BOLD
//        2 - ITALIC
//        3 - BOLDITALIC
        czcionka = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.CACHED);
        return new Font(czcionka, size, style);
    }

    private String dateFormat(LocalDate date) {

        String day = String.valueOf(date.getDayOfMonth());
        String month = "";

        if (date.getMonth().getValue() == 1) {
            month = "stycznia";
        }
        if (date.getMonth().getValue() == 2) {
            month = "lutego";
        }
        if (date.getMonth().getValue() == 3) {
            month = "marca";
        }
        if (date.getMonth().getValue() == 4) {
            month = "kwietnia";
        }
        if (date.getMonth().getValue() == 5) {
            month = "maja";
        }
        if (date.getMonth().getValue() == 6) {
            month = "czerwca";
        }
        if (date.getMonth().getValue() == 7) {
            month = "lipca";
        }
        if (date.getMonth().getValue() == 8) {
            month = "sierpnia";
        }
        if (date.getMonth().getValue() == 9) {
            month = "września";
        }
        if (date.getMonth().getValue() == 10) {
            month = "października";
        }
        if (date.getMonth().getValue() == 11) {
            month = "listopada";
        }
        if (date.getMonth().getValue() == 12) {
            month = "grudnia";
        }
        String year = String.valueOf(date.getYear());


        return day + " " + month + " " + year;


    }


    class PageStamper extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            final int currentPageNumber = writer.getCurrentPageNumber();

            try {
                final Rectangle pageSize = document.getPageSize();
                final PdfContentByte directContent = writer.getDirectContent();

                directContent.setColorFill(BaseColor.GRAY);
                directContent.setFontAndSize(BaseFont.createFont(), 10);

                directContent.setTextMatrix(pageSize.getRight(40), pageSize.getBottom(30));
                directContent.showText(String.valueOf(currentPageNumber));

            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}
