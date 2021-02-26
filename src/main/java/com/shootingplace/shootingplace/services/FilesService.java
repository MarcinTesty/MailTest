package com.shootingplace.shootingplace.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.enums.GunType;
import com.shootingplace.shootingplace.domain.models.FilesModel;
import com.shootingplace.shootingplace.repositories.*;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ClubRepository clubRepository;
    private final OtherPersonRepository otherPersonRepository;
    private final GunRepository gunRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public FilesService(MemberRepository memberRepository, AmmoEvidenceRepository ammoEvidenceRepository, FilesRepository filesRepository, TournamentRepository tournamentRepository, ClubRepository clubRepository, OtherPersonRepository otherPersonRepository, GunRepository gunRepository) {
        this.memberRepository = memberRepository;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
        this.filesRepository = filesRepository;
        this.tournamentRepository = tournamentRepository;
        this.clubRepository = clubRepository;
        this.otherPersonRepository = otherPersonRepository;
        this.gunRepository = gunRepository;
    }

    private FilesEntity createFileEntity(FilesModel filesModel) {

        FilesEntity filesEntity = Mapping.map(filesModel);
        LOG.info("Encja została zapisana");
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
        p2.add("                                    ");
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
                createFileEntity(filesModel);

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
                "- Wyrażam zgodę na przesyłanie mi informacji przez Klub Strzelecki „Dziesiątka” za pomocą środków komunikacji elektronicznej, w szczególności pocztą elektroniczną oraz w postaci sms-ów/mms-ów.\n" +
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
        Paragraph p22 = new Paragraph("miejscowość, data i podpis Klubowicza", new Font(czcionka, 11));
        Phrase p23 = new Phrase("                                                                 ");
        Phrase p24 = new Phrase("podpis przyjmującego");

        p1.add("Grupa ");
        p1.add(p2);
        p1.add("\n");
        p1.setIndentationLeft(190);
        p3.add(p4);
        p3.add("                                    ");
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
        p19.setIndentationLeft(40);
        p22.setIndentationLeft(25);
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
                createFileEntity(filesModel);


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
                createFileEntity(filesModel);

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
        String path1 = "Wniosek_o_przedluzenie_licencji_zawodniczej.pdf";
        PdfReader reader = new PdfReader(path1);
        PdfImportedPage page = writer.getImportedPage(reader, 1);

        document.newPage();
        cb.addTemplate(page, 0, 0);

        String licenseNumber = memberEntity.getLicense().getNumber();
        char[] P = memberEntity.getPesel().toCharArray();
        String phone = memberEntity.getPhoneNumber();
        String name = memberEntity.getSecondName().toUpperCase() + "  " + memberEntity.getFirstName().toUpperCase();
        String split = phone.substring(0, 3) + " ";
        String split1 = phone.substring(3, 6) + " ";
        String split2 = phone.substring(6, 9) + " ";
        String split3 = phone.substring(9, 12) + " ";
        String phoneSplit = split + split1 + split2 + split3;
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
        Paragraph pesel = new Paragraph(P[0] + "   " + P[1] + "   " + P[2] + "   " + P[3] + "   " + P[4] + "   " + P[5] + "  " + P[6] + "   " + P[7] + "   " + P[8] + "   " + P[9] + "   " + P[10] + "                                             " + phoneSplit, font(12, 0));
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
        ClubEntity club = clubRepository.findById(1).orElseThrow(EntityNotFoundException::new);
        for (int i = 0; i < pistol; i++) {
            float[] pointColumnWidths = {50, 20, 20, 5, 10, 2, 28};
            PdfPTable table = new PdfPTable(pointColumnWidths);


            PdfPCell cell = new PdfPCell(new Paragraph(pistolCollect.get(i).getName() + " " + club.getName(), font(9, 0)));
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
                createFileEntity(filesModel);

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

        Paragraph title = new Paragraph(tournamentEntity.getName().toUpperCase() + "\n" + "„DZIESIĄTKA” ŁÓDŹ", font(13, 1));
        Paragraph date = new Paragraph("Łódź, " + dateFormat(tournamentEntity.getDate()), font(10, 2));
        Paragraph newLine = new Paragraph("\n", font(10, 0));

        document.add(title);
        document.add(date);
        document.add(newLine);

        PdfPTable mainTable = new PdfPTable(1);
        mainTable.setWidthPercentage(100);


        for (int i = 0; i < tournamentEntity.getCompetitionsList().size(); i++) {
            if (!tournamentEntity.getCompetitionsList().get(i).getScoreList().isEmpty()) {
                CompetitionMembersListEntity competitionMembersListEntity = tournamentEntity.getCompetitionsList().get(i);
                competitionMembersListEntity.getScoreList().sort(Comparator.comparing(ScoreEntity::getScore).reversed());

                Paragraph competition = new Paragraph(competitionMembersListEntity.getName(), font(14, 1));
                competition.add("\n");
                document.add(competition);
                float[] pointColumnWidths = {25F, 150F, 150F, 25F, 25F, 25F};
                PdfPTable tableLabel = new PdfPTable(pointColumnWidths);
                PdfPCell cellLabel = new PdfPCell(new Paragraph("M-ce", font(10, 1)));
                PdfPCell cellLabel1 = new PdfPCell(new Paragraph("Imię i Nazwisko", font(10, 1)));
                PdfPCell cellLabel2 = new PdfPCell(new Paragraph("Klub", font(10, 1)));
                PdfPCell cellLabel3 = new PdfPCell(new Paragraph("Wynik", font(10, 1)));
                PdfPCell cellLabel4 = new PdfPCell(new Paragraph("10 x", font(10, 1)));
                PdfPCell cellLabel5 = new PdfPCell(new Paragraph("10 /", font(10, 1)));

                document.add(newLine);

                cellLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLabel1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLabel2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLabel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLabel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLabel5.setHorizontalAlignment(Element.ALIGN_CENTER);

                cellLabel.setBorder(0);
                cellLabel1.setBorder(0);
                cellLabel2.setBorder(0);
                cellLabel3.setBorder(0);
                cellLabel4.setBorder(0);
                cellLabel5.setBorder(0);

                tableLabel.setWidthPercentage(100F);
                tableLabel.addCell(cellLabel);
                tableLabel.addCell(cellLabel1);
                tableLabel.addCell(cellLabel2);
                tableLabel.addCell(cellLabel3);
                tableLabel.addCell(cellLabel4);
                tableLabel.addCell(cellLabel5);

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
                    String scoreIn = String.valueOf(competitionMembersListEntity.getScoreList().get(j).getInnerTen());
                    String scoreOut = String.valueOf(competitionMembersListEntity.getScoreList().get(j).getOuterTen());
                    if (competitionMembersListEntity.getScoreList().get(j).getInnerTen() == 0) {
                        scoreIn = "";
                    }
                    if (competitionMembersListEntity.getScoreList().get(j).getOuterTen() == 0) {
                        scoreOut = "";
                    }
                    PdfPTable playerTableLabel = new PdfPTable(pointColumnWidths);
                    PdfPCell playerCellLabel = new PdfPCell(new Paragraph(String.valueOf(j + 1), font(11, 0)));
                    PdfPCell playerCellLabel1 = new PdfPCell(new Paragraph(secondName + " " + firstName, font(11, 0)));
                    PdfPCell playerCellLabel2 = new PdfPCell(new Paragraph(club, font(11, 0)));
                    PdfPCell playerCellLabel3 = new PdfPCell(new Paragraph(String.valueOf(score).replace(".0", ""), font(11, 1)));
                    PdfPCell playerCellLabel4 = new PdfPCell(new Paragraph(scoreIn.replace(".0", ""), font(9, 2)));
                    PdfPCell playerCellLabel5 = new PdfPCell(new Paragraph(scoreOut.replace(".0", ""), font(9, 2)));


                    playerCellLabel.setBorder(0);
                    playerCellLabel1.setBorder(0);
                    playerCellLabel2.setBorder(0);
                    playerCellLabel3.setBorder(0);
                    playerCellLabel4.setBorder(0);
                    playerCellLabel5.setBorder(0);

                    playerCellLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    playerCellLabel1.setHorizontalAlignment(Element.ALIGN_LEFT);
                    playerCellLabel2.setHorizontalAlignment(Element.ALIGN_LEFT);
                    playerCellLabel3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    playerCellLabel4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    playerCellLabel5.setHorizontalAlignment(Element.ALIGN_CENTER);


                    playerTableLabel.setWidthPercentage(100F);

                    playerTableLabel.addCell(playerCellLabel);
                    playerTableLabel.addCell(playerCellLabel1);
                    playerTableLabel.addCell(playerCellLabel2);
                    playerTableLabel.addCell(playerCellLabel3);
                    playerTableLabel.addCell(playerCellLabel4);
                    playerTableLabel.addCell(playerCellLabel5);

                    document.add(playerTableLabel);

                }
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

        String arbiterRTS;
        String arbiterRTSClass;
        if (tournamentEntity.getCommissionRTSArbiter() != null) {
            arbiterRTS = tournamentEntity.getCommissionRTSArbiter().getFirstName() + " " + tournamentEntity.getCommissionRTSArbiter().getSecondName();
            arbiterRTSClass = tournamentEntity.getCommissionRTSArbiter().getMemberPermissions().getArbiterClass();
        } else {
            arbiterRTS = tournamentEntity.getOtherCommissionRTSArbiter().getFirstName() + " " + tournamentEntity.getOtherCommissionRTSArbiter().getSecondName();
            arbiterRTSClass = tournamentEntity.getOtherCommissionRTSArbiter().getPermissionsEntity().getArbiterClass();
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
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    public FilesEntity CertificateOfClubMembership(String memberUUID) throws IOException, DocumentException {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        ClubEntity clubEntity = clubRepository.findById(1).orElseThrow(EntityNotFoundException::new);
        String fileName = "Zaświadczenie" + memberEntity.getFirstName().concat(" " + memberEntity.getSecondName()) + ".pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        PdfPTable mainTable = new PdfPTable(1);

        document.add(mainTable);

        String policeAddress = "\nKomendant Wojewódzki Policji w Łodzi\nWydział Postępowań Administracyjnych\n 90-144 Łódź, ul. Sienkiewicza 26";

        Paragraph date = new Paragraph("Łódź, " + LocalDate.now(), font(12, 0));
        date.setAlignment(2);
        Paragraph police = new Paragraph(policeAddress, font(12, 0));
        police.setAlignment(2);
        document.add(date);
        document.add(police);

        Paragraph title = new Paragraph("\n\nZaświadczenie\n\n", font(14, 1));
        title.setAlignment(1);

        Paragraph par1 = new Paragraph("" + getSex(memberEntity.getPesel()) + " " + memberEntity.getFirstName().concat(" " + memberEntity.getSecondName()) + " PESEL: " + memberEntity.getPesel() + " jest czynnym członkiem Klubu Strzeleckiego „Dziesiątka” LOK w Łodzi. Numer legitymacji klubowej : " + memberEntity.getLegitimationNumber() + " ." +
                "Uczestniczy w zawodach i treningach strzeleckich osiągając bardzo dobre wyniki. " +
                "Czynnie uczestniczy w życiu Klubu. ", font(12, 0));
        par1.setFirstLineIndent(40);

        Paragraph par2 = new Paragraph(getSex(memberEntity.getPesel()) + " " + memberEntity.getFirstName().concat(" " + memberEntity.getSecondName()) + " wyraża chęć pogłębiania swojej wiedzy i umiejętności w sporcie strzeleckim przez współzawodnictwo w różnych konkurencjach strzeleckich.", font(12, 0));
        par2.setFirstLineIndent(40);

        Paragraph par3 = new Paragraph(getSex(memberEntity.getPesel()) + " " + memberEntity.getFirstName().concat(" " + memberEntity.getSecondName()) + " posiada Patent Strzelecki PZSS oraz ważną Licencję Zawodniczą PZSS na rok " + clubEntity.getLicenseNumber().substring(5), font(12, 0));
        par3.setFirstLineIndent(40);

        Paragraph par4 = new Paragraph("Klub strzelecki „Dziesiątka” LOK w Łodzi jest członkiem Polskiego Związku Strzelectwa Sportowego i posiada Licencję Klubową nr LK-" + clubEntity.getLicenseNumber() + ", jest również Członkiem Łódzkiego Związku Strzelectwa Sportowego, zarejestrowany pod numerem ewidencyjnym 6.", font(12, 0));
        par4.setFirstLineIndent(40);

        Paragraph par5 = new Paragraph(getSex(memberEntity.getPesel()) + " " + memberEntity.getFirstName().concat(" " + memberEntity.getSecondName()) + " wystąpił z prośbą o wydanie niniejszego zaświadczenia, skutkiem którego będzie złożenie wniosku o pozwolenie na broń sportową do celów sportowych. \n\n\n", font(12, 0));
        par5.setFirstLineIndent(40);

        Paragraph par6 = new Paragraph("Sporządzono w 2 egz. \n\n", font(12, 0));

        Paragraph par7 = new Paragraph("Egz. Nr 1 – a/a\n" +
                "Egz. Nr 2 – Adresat", font(12, 0));
        par7.setIndentationLeft(40);
        document.add(title);
        document.add(par1);
        document.add(par2);
        document.add(par3);
        document.add(par4);
        document.add(par5);
        document.add(par6);
        document.add(par7);

        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    public FilesEntity getStartsMetric(String memberUUID, String otherID, String tournamentUUID, List<String> competitions, String startNumber) throws IOException, DocumentException {
        String name;

        if (otherID != null) {
            OtherPersonEntity otherPersonEntity = otherPersonRepository.findById(Integer.valueOf(otherID)).orElseThrow(EntityNotFoundException::new);
            name = otherPersonEntity.getSecondName().concat(" " + otherPersonEntity.getFirstName() + " " + otherPersonEntity.getClub().getName());
        } else {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            name = memberEntity.getSecondName().concat(" " + memberEntity.getFirstName() + " " + memberEntity.getClub().getName());
        }
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);

        ClubEntity clubEntity = clubRepository.findById(1).orElseThrow(EntityNotFoundException::new);

        String fileName = "metryki_" + name + ".pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();

        Paragraph newLine = new Paragraph("\n", font(12, 0));
        for (int j = 0; j < competitions.size(); j++) {
            Paragraph par1 = new Paragraph(tournamentEntity.getName().toUpperCase() + " " + clubEntity.getName(), font(12, 1));
            par1.setAlignment(1);

            Paragraph par2 = new Paragraph(name.toUpperCase(), font(12, 0));
            par2.setAlignment(1);

            Chunk chunk = new Chunk("                              Nr. " + startNumber, font(18, 1));
            par2.add(chunk);

            Paragraph par3 = new Paragraph(competitions.get(j), font(12, 1));
            par3.setAlignment(1);

            Paragraph par4 = new Paragraph("Podpis sędziego .............................", font(12, 0));
            Chunk chunk1 = new Chunk("                                   Podpis zawodnika .............................          ", font(12, 0));
            Chunk chunk2 = new Chunk(" Nr. " + startNumber, font(18, 1));

            par4.add(chunk1);
            par4.add(chunk2);

            float[] pointColumnWidths = {25F, 25F, 25F, 25F, 25F, 25F, 25F, 25F, 25F, 25F, 60F};
            PdfPTable table = new PdfPTable(pointColumnWidths);
            PdfPTable table1 = new PdfPTable(pointColumnWidths);

            for (int i = 0; i <= 11; i++) {
                Paragraph p;
                if (i < 10) {
                    p = new Paragraph(String.valueOf(i + 1), font(14, 0));
                } else {
                    p = new Paragraph("SUMA", font(14, 1));

                }
                PdfPCell cell = new PdfPCell(p);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);

            }
            for (int i = 0; i <= 11; i++) {
                String s = " ";
                    Chunk c = new Chunk(s,font(28,0));
                Paragraph p = new Paragraph(c);
                PdfPCell cell = new PdfPCell(p);
                table1.addCell(cell);

            }

            document.add(par1);
            document.add(par2);
            document.add(par3);
            document.add(newLine);
            document.add(table);
            document.add(table1);
            document.add(newLine);
            document.add(par4);
            if (j < 7) {
                document.add(newLine);
                document.add(newLine);
            }
        }

        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    public FilesEntity getAllMembersToTable(boolean condition) throws IOException, DocumentException {

        String fileName = "Lista_klubowiczów_na_dzień " + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();

        List<MemberEntity> all = memberRepository.findAll().stream().filter(f -> !f.getErased()).filter(f -> f.getAdult().equals(condition)).sorted(Comparator.comparing(MemberEntity::getSecondName)).collect(Collectors.toList());

        String hour = String.valueOf(LocalTime.now().getHour());
        String minute = String.valueOf(LocalTime.now().getMinute());
        if (Integer.parseInt(minute) < 10) {

            minute = "0" + minute;

        }

        String now = hour + ":" + minute;

        Paragraph title = new Paragraph("Lista klubowiczów na dzień " + LocalDate.now() + " " + now, font(14, 1));
        Paragraph newLine = new Paragraph("\n", font(14, 0));
        document.add(title);
        document.add(newLine);

        float[] pointColumnWidths = {4F, 58F, 10F, 12F, 12F, 12F};


        PdfPTable titleTable = new PdfPTable(pointColumnWidths);

        titleTable.setWidthPercentage(100);

        PdfPCell lp = new PdfPCell(new Paragraph("lp", font(12, 0)));
        PdfPCell name = new PdfPCell(new Paragraph("Nazwisko Imię", font(12, 0)));
        PdfPCell LegNumber = new PdfPCell(new Paragraph("legitymacja", font(12, 0)));
        PdfPCell inDate = new PdfPCell(new Paragraph("Data zapisu", font(12, 0)));
        PdfPCell contributionDate = new PdfPCell(new Paragraph("Data opłacenia składki", font(12, 0)));
        PdfPCell contributionValidThru = new PdfPCell(new Paragraph("Składka ważna do", font(12, 0)));

        titleTable.addCell(lp);
        titleTable.addCell(name);
        titleTable.addCell(LegNumber);
        titleTable.addCell(inDate);
        titleTable.addCell(contributionDate);
        titleTable.addCell(contributionValidThru);

        document.add(titleTable);
        document.add(newLine);

        for (int i = 0; i < all.size(); i++) {
            MemberEntity memberEntity = all.get(i);
            PdfPTable memberTable = new PdfPTable(pointColumnWidths);

            memberTable.setWidthPercentage(100);

            PdfPCell lpCell = new PdfPCell(new Paragraph(String.valueOf(i + 1), font(12, 0)));
            PdfPCell nameCell = new PdfPCell(new Paragraph(memberEntity.getSecondName().concat(" " + memberEntity.getFirstName()), font(12, 0)));
            PdfPCell legNumberCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getLegitimationNumber()), font(12, 0)));
            PdfPCell inDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getJoinDate()), font(12, 0)));
            PdfPCell contributionDateCell;
            PdfPCell contributionValidThruCell;
            if (memberEntity.getHistory().getContributionList().size() > 0) {

                contributionDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getHistory().getContributionList().get(0).getPaymentDay()), font(12, 0)));
                contributionValidThruCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getHistory().getContributionList().get(0).getValidThru()), font(12, 0)));

            } else {

                contributionDateCell = new PdfPCell(new Paragraph("BRAK SKŁADEK", font(12, 1)));
                contributionValidThruCell = new PdfPCell(new Paragraph("BRAK SKŁADEK", font(12, 1)));

            }

            memberTable.addCell(lpCell);
            memberTable.addCell(nameCell);
            memberTable.addCell(legNumberCell);
            memberTable.addCell(inDateCell);
            memberTable.addCell(contributionDateCell);
            memberTable.addCell(contributionValidThruCell);

            document.add(memberTable);
        }


        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    public FilesEntity getJudge(String tournamentUUID) throws IOException, DocumentException {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);

        String fileName = "Lista_sędziów_na_zawodach_" + tournamentEntity.getName() + ".pdf";

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();

        Paragraph title = new Paragraph(tournamentEntity.getName().toUpperCase() + "\n" + "„DZIESIĄTKA” ŁÓDŹ", font(13, 1));
        Paragraph date = new Paragraph("Łódź, " + dateFormat(tournamentEntity.getDate()), font(10, 2));

        Paragraph listTitle = new Paragraph("WYKAZ SĘDZIÓW", font(13, 0));
        Paragraph newLine = new Paragraph("\n", font(13, 0));

        Paragraph subTitle = new Paragraph("\nSędzia Główny", font(13, 0));
        Paragraph subTitle1 = new Paragraph("\nPrzewodniczący Komisji RTS", font(13, 0));

        String mainArbiter;

        if (tournamentEntity.getMainArbiter() != null) {
            mainArbiter = tournamentEntity.getMainArbiter().getSecondName().concat(" " + tournamentEntity.getMainArbiter().getFirstName() + " " + tournamentEntity.getMainArbiter().getMemberPermissions().getArbiterClass());
        } else {
            mainArbiter = tournamentEntity.getOtherMainArbiter().getSecondName().concat(" " + tournamentEntity.getOtherMainArbiter().getFirstName() + " " + tournamentEntity.getOtherMainArbiter().getPermissionsEntity().getArbiterClass());
        }

        Paragraph mainArbiterOnTournament = new Paragraph(mainArbiter, font(12, 0));

        String commissionRTSArbiter;

        if (tournamentEntity.getMainArbiter() != null) {
            commissionRTSArbiter = tournamentEntity.getCommissionRTSArbiter().getSecondName().concat(" " + tournamentEntity.getCommissionRTSArbiter().getFirstName() + " " + tournamentEntity.getCommissionRTSArbiter().getMemberPermissions().getArbiterClass());
        } else {
            commissionRTSArbiter = tournamentEntity.getOtherCommissionRTSArbiter().getSecondName().concat(" " + tournamentEntity.getOtherCommissionRTSArbiter().getFirstName() + " " + tournamentEntity.getOtherCommissionRTSArbiter().getPermissionsEntity().getArbiterClass());
        }

        Paragraph commissionRTSArbiterOnTournament = new Paragraph(commissionRTSArbiter, font(12, 0));

        Paragraph others = new Paragraph("\nSędziowie Stanowiskowi\n", font(12, 0));
        Paragraph others1 = new Paragraph("\nSędziowie Biura Obliczeń\n", font(12, 0));

        document.add(title);
        document.add(date);
        document.add(newLine);
        document.add(listTitle);
        document.add(subTitle);
        document.add(mainArbiterOnTournament);
        document.add(subTitle1);
        document.add(commissionRTSArbiterOnTournament);

        if (tournamentEntity.getArbitersList().size() > 0 || tournamentEntity.getOtherArbitersList().size() > 0) {
            document.add(others);
        }

        List<MemberEntity> arbitersList = tournamentEntity.getArbitersList();
        if (arbitersList.size() > 0) {
            for (MemberEntity entity : arbitersList) {
                Paragraph otherArbiter = new Paragraph(entity.getSecondName().concat(" " + entity.getFirstName() + " " + entity.getMemberPermissions().getArbiterClass()), font(12, 0));
                document.add(otherArbiter);
            }
        }

        List<OtherPersonEntity> otherArbitersList = tournamentEntity.getOtherArbitersList();
        if (otherArbitersList.size() > 0) {

            for (OtherPersonEntity personEntity : otherArbitersList) {
                Paragraph otherPersonArbiter = new Paragraph(personEntity.getSecondName().concat(" " + personEntity.getFirstName() + " " + personEntity.getPermissionsEntity().getArbiterClass()), font(12, 0));
                document.add(otherPersonArbiter);
            }

        }

        if (tournamentEntity.getArbitersRTSList().size() > 0 || tournamentEntity.getOtherArbitersRTSList().size() > 0) {
            document.add(others1);
        }

        List<MemberEntity> arbitersRTSList = tournamentEntity.getArbitersRTSList();
        if (arbitersRTSList.size() > 0) {
            for (MemberEntity entity : arbitersRTSList) {
                Paragraph otherRTSArbiter = new Paragraph(entity.getSecondName().concat(" " + entity.getFirstName() + " " + entity.getMemberPermissions().getArbiterClass()), font(12, 0));
                document.add(otherRTSArbiter);
            }
        }

        List<OtherPersonEntity> otherArbitersRTSList = tournamentEntity.getOtherArbitersRTSList();
        if (otherArbitersRTSList.size() > 0) {
            for (OtherPersonEntity personEntity : otherArbitersRTSList) {
                Paragraph otherPersonRTSArbiter = new Paragraph(personEntity.getSecondName().concat(" " + personEntity.getFirstName() + " " + personEntity.getPermissionsEntity().getArbiterClass()), font(12, 0));
                document.add(otherPersonRTSArbiter);
            }
        }


        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;

    }

    public FilesEntity getAllMembersWithLicenceNotValidAndContributionNotValid() throws IOException, DocumentException {

        String fileName = "Lista_osób_bez_składek_z_nieważnymi_licencjami_na_dzień" + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        String minute;
        if (LocalTime.now().getMinute() < 10) {
            minute = "0" + LocalTime.now().getMinute();
        } else {
            minute = String.valueOf(LocalTime.now().getMinute());
        }
        String now = LocalTime.now().getHour() + ":" + minute;
        Paragraph title = new Paragraph("Lista osób bez składek z nieważnymi licencjami na dzień " + LocalDate.now() + " " + now, font(14, 1));
        Paragraph newLine = new Paragraph("\n", font(14, 0));


        document.add(title);
        document.add(newLine);
        LocalDate notValidLicense = LocalDate.now().minusYears(1);
        LocalDate notValidContribution = LocalDate.of(LocalDate.now().getYear(), 12, 31).minusYears(1);
        List<MemberEntity> memberEntityList = memberRepository.findAll().stream()
                .filter(f -> !f.getErased())
                .filter(f -> f.getLicense().getNumber() != null)
                .filter(f -> !f.getLicense().isValid())
                .filter(f -> f.getLicense().getValidThru().isBefore(notValidLicense))
                .filter(f -> f.getHistory().getContributionList().get(0).getValidThru().isBefore(notValidContribution))
                .sorted(Comparator.comparing(MemberEntity::getSecondName))
                .collect(Collectors.toList());
        // [lp] [Naz imię leg] [licNum] [licDate] [contrDate]
        float[] pointColumnWidths = {4F, 54F, 14F, 14F, 14F};


        PdfPTable titleTable = new PdfPTable(pointColumnWidths);

        PdfPCell lp = new PdfPCell(new Paragraph("lp", font(12, 0)));
        PdfPCell nameWithLegitimation = new PdfPCell(new Paragraph("Nazwisko Imię legitymacja", font(12, 0)));
        PdfPCell licenceNumber = new PdfPCell(new Paragraph("numer licencji", font(12, 0)));
        PdfPCell licenceDate = new PdfPCell(new Paragraph("licencja ważna do", font(12, 0)));
        PdfPCell contributionDate = new PdfPCell(new Paragraph("Składka ważna do", font(12, 0)));

        titleTable.setWidthPercentage(100);

        titleTable.addCell(lp);
        titleTable.addCell(nameWithLegitimation);
        titleTable.addCell(licenceNumber);
        titleTable.addCell(licenceDate);
        titleTable.addCell(contributionDate);

        document.add(titleTable);

        document.add(newLine);

        for (int i = 0; i < memberEntityList.size(); i++) {

            MemberEntity memberEntity = memberEntityList.get(i);

            String memberEntityName = memberEntity.getSecondName().concat(" " + memberEntity.getFirstName() + " " + memberEntity.getLegitimationNumber());

            PdfPTable memberTable = new PdfPTable(pointColumnWidths);

            PdfPCell lpCell = new PdfPCell(new Paragraph(String.valueOf(i + 1), font(12, 0)));
            PdfPCell nameWithLegitimationCell = new PdfPCell(new Paragraph(memberEntityName, font(12, 0)));
            PdfPCell licenceNumberCell = new PdfPCell(new Paragraph(memberEntity.getLicense().getNumber(), font(12, 0)));
            PdfPCell licenceDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getLicense().getValidThru()), font(12, 0)));
            PdfPCell contributionDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getHistory().getContributionList().get(0).getValidThru()), font(12, 0)));

            memberTable.setWidthPercentage(100);

            memberTable.addCell(lpCell);
            memberTable.addCell(nameWithLegitimationCell);
            memberTable.addCell(licenceNumberCell);
            memberTable.addCell(licenceDateCell);
            memberTable.addCell(contributionDateCell);

            document.add(memberTable);

        }


        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }


    public FilesEntity getAllMembersToErased() throws IOException, DocumentException {

        String fileName = "Lista_osób_do_usunięcia_na_dzień" + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        String minute;
        if (LocalTime.now().getMinute() < 10) {
            minute = "0" + LocalTime.now().getMinute();
        } else {
            minute = String.valueOf(LocalTime.now().getMinute());
        }
        String now = LocalTime.now().getHour() + ":" + minute;
        Paragraph title = new Paragraph("Lista osób do usunięcia na dzień " + LocalDate.now() + " " + now, font(14, 1));
        Paragraph newLine = new Paragraph("\n", font(14, 0));


        document.add(title);
        document.add(newLine);
        LocalDate notValidContribution = LocalDate.of(LocalDate.now().getYear(), 12, 31).minusYears(2);
        List<MemberEntity> memberEntityList = memberRepository.findAll().stream()
                .filter(f -> !f.getErased())
                .filter(f -> !f.getActive())
                .filter(f -> !f.getHistory().getContributionList().isEmpty() && f.getHistory().getContributionList().get(0).getValidThru().minusDays(1).isBefore(notValidContribution))
                .sorted(Comparator.comparing(MemberEntity::getSecondName))
                .collect(Collectors.toList());
        // [lp] [Naz imię leg] [licNum] [licDate] [contrDate]
        float[] pointColumnWidths = {4F, 54F, 14F, 14F, 14F};


        PdfPTable titleTable = new PdfPTable(pointColumnWidths);

        PdfPCell lp = new PdfPCell(new Paragraph("lp", font(12, 0)));
        PdfPCell nameWithLegitimation = new PdfPCell(new Paragraph("Nazwisko Imię legitymacja", font(12, 0)));
        PdfPCell licenceNumber = new PdfPCell(new Paragraph("numer licencji", font(12, 0)));
        PdfPCell licenceDate = new PdfPCell(new Paragraph("licencja ważna do", font(12, 0)));
        PdfPCell contributionDate = new PdfPCell(new Paragraph("Składka ważna do", font(12, 0)));

        titleTable.setWidthPercentage(100);

        titleTable.addCell(lp);
        titleTable.addCell(nameWithLegitimation);
        titleTable.addCell(licenceNumber);
        titleTable.addCell(licenceDate);
        titleTable.addCell(contributionDate);

        document.add(titleTable);

        document.add(newLine);

        for (int i = 0; i < memberEntityList.size(); i++) {

            MemberEntity memberEntity = memberEntityList.get(i);

            String memberEntityName = memberEntity.getSecondName().concat(" " + memberEntity.getFirstName() + " " + memberEntity.getLegitimationNumber());

            PdfPTable memberTable = new PdfPTable(pointColumnWidths);

            PdfPCell lpCell = new PdfPCell(new Paragraph(String.valueOf(i + 1), font(12, 0)));
            PdfPCell nameWithLegitimationCell = new PdfPCell(new Paragraph(memberEntityName, font(12, 0)));
            PdfPCell licenceNumberCell;
            if (memberEntity.getLicense().getNumber() != null) {
                licenceNumberCell = new PdfPCell(new Paragraph(memberEntity.getLicense().getNumber(), font(12, 0)));
            } else {
                licenceNumberCell = new PdfPCell(new Paragraph("", font(12, 0)));
            }
            PdfPCell licenceDateCell;
            if (memberEntity.getLicense().getNumber() != null) {
                licenceDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getLicense().getValidThru()), font(12, 0)));
            } else {
                licenceDateCell = new PdfPCell(new Paragraph("", font(12, 0)));
            }
            PdfPCell contributionDateCell;
            if (memberEntity.getHistory().getContributionList().size() > 0) {
                contributionDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getHistory().getContributionList().get(0).getValidThru()), font(12, 0)));
            } else {
                contributionDateCell = new PdfPCell(new Paragraph("BRAK SKŁADEK", font(12, 0)));
            }
            memberTable.setWidthPercentage(100);

            memberTable.addCell(lpCell);
            memberTable.addCell(nameWithLegitimationCell);
            memberTable.addCell(licenceNumberCell);
            memberTable.addCell(licenceDateCell);
            memberTable.addCell(contributionDateCell);

            document.add(memberTable);

        }


        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    public FilesEntity getAllMembersWithLicenceValidAndContributionNotValid() throws IOException, DocumentException {


        String fileName = "Lista_osób_bez_składek_z_ważnymi_licencjami_na_dzień" + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        String minute;
        if (LocalTime.now().getMinute() < 10) {
            minute = "0" + LocalTime.now().getMinute();
        } else {
            minute = String.valueOf(LocalTime.now().getMinute());
        }
        String now = LocalTime.now().getHour() + ":" + minute;
        Paragraph title = new Paragraph("Lista osób bez składek z ważnymi licencjami na dzień " + LocalDate.now() + " " + now, font(14, 1));
        Paragraph newLine = new Paragraph("\n", font(14, 0));


        document.add(title);
        document.add(newLine);

        List<MemberEntity> memberEntityList = memberRepository.findAll().stream()
                .filter(f -> !f.getErased())
                .filter(f -> f.getLicense().getNumber() != null)
                .filter(f -> f.getLicense().isValid())
                .filter(f -> !f.getActive())
                .sorted(Comparator.comparing(MemberEntity::getSecondName))
                .collect(Collectors.toList());
        // [lp] [Naz imię leg] [licNum] [licDate] [contrDate]
        float[] pointColumnWidths = {4F, 54F, 14F, 14F, 14F};


        PdfPTable titleTable = new PdfPTable(pointColumnWidths);

        PdfPCell lp = new PdfPCell(new Paragraph("lp", font(12, 0)));
        PdfPCell nameWithLegitimation = new PdfPCell(new Paragraph("Nazwisko Imię legitymacja", font(12, 0)));
        PdfPCell licenceNumber = new PdfPCell(new Paragraph("numer licencji", font(12, 0)));
        PdfPCell licenceDate = new PdfPCell(new Paragraph("licencja ważna do", font(12, 0)));
        PdfPCell contributionDate = new PdfPCell(new Paragraph("Składka ważna do", font(12, 0)));

        titleTable.setWidthPercentage(100);

        titleTable.addCell(lp);
        titleTable.addCell(nameWithLegitimation);
        titleTable.addCell(licenceNumber);
        titleTable.addCell(licenceDate);
        titleTable.addCell(contributionDate);

        document.add(titleTable);

        document.add(newLine);

        for (int i = 0; i < memberEntityList.size(); i++) {

            MemberEntity memberEntity = memberEntityList.get(i);

            String memberEntityName = memberEntity.getSecondName().concat(" " + memberEntity.getFirstName() + " " + memberEntity.getLegitimationNumber());

            PdfPTable memberTable = new PdfPTable(pointColumnWidths);

            PdfPCell lpCell = new PdfPCell(new Paragraph(String.valueOf(i + 1), font(12, 0)));
            PdfPCell nameWithLegitimationCell = new PdfPCell(new Paragraph(memberEntityName, font(12, 0)));
            PdfPCell licenceNumberCell = new PdfPCell(new Paragraph(memberEntity.getLicense().getNumber(), font(12, 0)));
            PdfPCell licenceDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getLicense().getValidThru()), font(12, 0)));
            PdfPCell contributionDateCell;
            if (memberEntity.getHistory().getContributionList().size() > 0) {
                contributionDateCell = new PdfPCell(new Paragraph(String.valueOf(memberEntity.getHistory().getContributionList().get(0).getValidThru()), font(12, 0)));
            } else {
                contributionDateCell = new PdfPCell(new Paragraph("BRAK SKŁADEK", font(12, 0)));
            }
            memberTable.setWidthPercentage(100);

            memberTable.addCell(lpCell);
            memberTable.addCell(nameWithLegitimationCell);
            memberTable.addCell(licenceNumberCell);
            memberTable.addCell(licenceDateCell);
            memberTable.addCell(contributionDateCell);

            document.add(memberTable);

        }


        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;

    }

    public FilesEntity getGunRegistry() throws IOException, DocumentException {


        String fileName = "Lista_broni_w_magazynie_na_dzień" + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        String minute;
        if (LocalTime.now().getMinute() < 10) {
            minute = "0" + LocalTime.now().getMinute();
        } else {
            minute = String.valueOf(LocalTime.now().getMinute());
        }
        String now = LocalTime.now().getHour() + ":" + minute;
        Paragraph title = new Paragraph("Lista jednostek broni w magazynie na dzień " + LocalDate.now() + " " + now, font(14, 1));
        Paragraph newLine = new Paragraph("\n", font(14, 0));


        document.add(title);
        document.add(newLine);

        float[] pointColumnWidths = {4F, 16F, 10F, 10F, 10F, 10F, 10F, 10F};

        Paragraph lp = new Paragraph("Lp", font(12, 0));
        Paragraph modelName = new Paragraph("Marka i Model", font(12, 0));
        Paragraph caliberAndProductionYear = new Paragraph("Kaliber i rok produkcji", font(12, 0));
        Paragraph serialNumber = new Paragraph("Numer i seria", font(12, 0));
        Paragraph recordInEvidenceBook = new Paragraph("Poz. z książki ewidencji", font(12, 0));
        Paragraph numberOfMagazines = new Paragraph("Magazynki", font(12, 0));
        Paragraph gunCertificateSerialNumber = new Paragraph("Numer świadectwa", font(12, 0));
        Paragraph basisForPurchaseOrAssignment = new Paragraph("Podstawa wpisu", font(12, 0));


        PdfPTable titleTable = new PdfPTable(pointColumnWidths);
        titleTable.setWidthPercentage(100);

        PdfPCell lpCell = new PdfPCell(lp);
        PdfPCell modelNameCell = new PdfPCell(modelName);
        PdfPCell caliberAndProductionYearCell = new PdfPCell(caliberAndProductionYear);
        PdfPCell serialNumberCell = new PdfPCell(serialNumber);
        PdfPCell recordInEvidenceBookCell = new PdfPCell(recordInEvidenceBook);
        PdfPCell numberOfMagazinesCell = new PdfPCell(numberOfMagazines);
        PdfPCell gunCertificateSerialNumberCell = new PdfPCell(gunCertificateSerialNumber);
        PdfPCell basisForPurchaseOrAssignmentCell = new PdfPCell(basisForPurchaseOrAssignment);

        lpCell.setHorizontalAlignment(1);
        lpCell.setVerticalAlignment(1);
        modelNameCell.setHorizontalAlignment(1);
        modelNameCell.setVerticalAlignment(1);
        caliberAndProductionYearCell.setHorizontalAlignment(1);
        caliberAndProductionYearCell.setVerticalAlignment(1);
        serialNumberCell.setHorizontalAlignment(1);
        serialNumberCell.setVerticalAlignment(1);
        recordInEvidenceBookCell.setHorizontalAlignment(1);
        recordInEvidenceBookCell.setVerticalAlignment(1);
        numberOfMagazinesCell.setHorizontalAlignment(1);
        numberOfMagazinesCell.setVerticalAlignment(1);
        gunCertificateSerialNumberCell.setHorizontalAlignment(1);
        gunCertificateSerialNumberCell.setVerticalAlignment(1);
        basisForPurchaseOrAssignmentCell.setHorizontalAlignment(1);
        basisForPurchaseOrAssignmentCell.setVerticalAlignment(1);


        titleTable.addCell(lpCell);
        titleTable.addCell(modelNameCell);
        titleTable.addCell(caliberAndProductionYearCell);
        titleTable.addCell(serialNumberCell);
        titleTable.addCell(recordInEvidenceBookCell);
        titleTable.addCell(numberOfMagazinesCell);
        titleTable.addCell(gunCertificateSerialNumberCell);
        titleTable.addCell(basisForPurchaseOrAssignmentCell);

        document.add(titleTable);

        List<String> list = new ArrayList<>();

        GunType[] values = GunType.values();
        Arrays.stream(values).forEach(e -> list.add(e.getName()));

        for (int i = 0; i < list.size(); i++) {

            int finalI = i;
            List<GunEntity> collect = gunRepository.findAll()
                    .stream()
                    .filter(f -> f.getGunType().equals(list.get(finalI)))
                    .sorted(Comparator.comparing(GunEntity::getCaliber).thenComparing(GunEntity::getModelName))
                    .collect(Collectors.toList());
            if (collect.size() > 0) {
                Paragraph gunTypeName = new Paragraph(list.get(i), font(12, 1));
                gunTypeName.setAlignment(1);
                document.add(gunTypeName);
                document.add(newLine);

                for (int j = 0; j < collect.size(); j++) {

                    GunEntity gun = collect.get(j);

                    PdfPTable gunTable = new PdfPTable(pointColumnWidths);
                    gunTable.setWidthPercentage(100);

                    Paragraph lpGun = new Paragraph(String.valueOf(j + 1), font(12, 0));
                    Paragraph modelNameGun = new Paragraph(gun.getModelName(), font(12, 0));
                    Paragraph caliberAndProductionYearGun;
                    if (gun.getProductionYear() != null && !gun.getProductionYear().isEmpty()) {
                        caliberAndProductionYearGun = new Paragraph(gun.getCaliber() + "\nrok " + gun.getProductionYear(), font(12, 0));

                    } else {
                        caliberAndProductionYearGun = new Paragraph(gun.getCaliber(), font(12, 0));
                    }
                    Paragraph serialNumberGun = new Paragraph(gun.getSerialNumber(), font(12, 0));
                    Paragraph recordInEvidenceBookGun = new Paragraph(gun.getRecordInEvidenceBook(), font(12, 0));
                    Paragraph numberOfMagazinesGun = new Paragraph(gun.getNumberOfMagazines(), font(12, 0));
                    Paragraph gunCertificateSerialNumberGun = new Paragraph(gun.getGunCertificateSerialNumber(), font(12, 0));
                    Paragraph basisForPurchaseOrAssignmentGun = new Paragraph(gun.getBasisForPurchaseOrAssignment(), font(12, 0));

                    PdfPCell lpGunCell = new PdfPCell(lpGun);
                    PdfPCell modelNameGunCell = new PdfPCell(modelNameGun);
                    PdfPCell caliberAndProductionYearGunCell = new PdfPCell(caliberAndProductionYearGun);
                    PdfPCell serialNumberGunCell = new PdfPCell(serialNumberGun);
                    PdfPCell recordInEvidenceBookGunCell = new PdfPCell(recordInEvidenceBookGun);
                    PdfPCell numberOfMagazinesGunCell = new PdfPCell(numberOfMagazinesGun);
                    PdfPCell gunCertificateSerialNumberGunCell = new PdfPCell(gunCertificateSerialNumberGun);
                    PdfPCell basisForPurchaseOrAssignmentGunCell = new PdfPCell(basisForPurchaseOrAssignmentGun);

                    lpGunCell.setHorizontalAlignment(1);
                    lpGunCell.setVerticalAlignment(1);
                    modelNameGunCell.setHorizontalAlignment(1);
                    modelNameGunCell.setVerticalAlignment(1);
                    caliberAndProductionYearGunCell.setHorizontalAlignment(1);
                    caliberAndProductionYearGunCell.setVerticalAlignment(1);
                    serialNumberGunCell.setHorizontalAlignment(1);
                    serialNumberGunCell.setVerticalAlignment(1);
                    recordInEvidenceBookGunCell.setHorizontalAlignment(1);
                    recordInEvidenceBookGunCell.setVerticalAlignment(1);
                    numberOfMagazinesGunCell.setHorizontalAlignment(1);
                    numberOfMagazinesGunCell.setVerticalAlignment(1);
                    gunCertificateSerialNumberGunCell.setHorizontalAlignment(1);
                    gunCertificateSerialNumberGunCell.setVerticalAlignment(1);
                    basisForPurchaseOrAssignmentGunCell.setHorizontalAlignment(1);
                    basisForPurchaseOrAssignmentGunCell.setVerticalAlignment(1);

                    gunTable.addCell(lpGunCell);
                    gunTable.addCell(modelNameGunCell);
                    gunTable.addCell(caliberAndProductionYearGunCell);
                    gunTable.addCell(serialNumberGunCell);
                    gunTable.addCell(recordInEvidenceBookGunCell);
                    gunTable.addCell(numberOfMagazinesGunCell);
                    gunTable.addCell(gunCertificateSerialNumberGunCell);
                    gunTable.addCell(basisForPurchaseOrAssignmentGunCell);

                    document.add(gunTable);
                }
                document.add(newLine);

            }

        }

        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;

    }

    public FilesEntity getGunTransportCertificate(List<String> guns, LocalDate date, LocalDate date1) throws IOException, DocumentException {


        String fileName = "Lista_broni_do_przewozu_na_dzień" + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));
        document.open();
        document.addTitle(fileName);
        document.addCreationDate();
        String minute;
        if (LocalTime.now().getMinute() < 10) {
            minute = "0" + LocalTime.now().getMinute();
        } else {
            minute = String.valueOf(LocalTime.now().getMinute());
        }
        String now = LocalTime.now().getHour() + ":" + minute;
        Paragraph title = new Paragraph("Lista jednostek broni do przewozu od dnia " + date + " do dnia " + date1, font(14, 1));
        Paragraph subtitle = new Paragraph("Wystawiono dnia " + LocalDate.now() + " o godzinie " + now, font(14, 1));
        Paragraph newLine = new Paragraph("\n", font(14, 0));


        document.add(title);
        document.add(subtitle);
        document.add(newLine);

        float[] pointColumnWidths = {4F, 16F, 12F, 12F, 12F, 12F, 12F};

        Paragraph lp = new Paragraph("Lp", font(12, 0));
        Paragraph modelName = new Paragraph("Marka i Model", font(12, 0));
        Paragraph caliberAndProductionYear = new Paragraph("Kaliber i rok produkcji", font(12, 0));
        Paragraph serialNumber = new Paragraph("Numer i seria", font(12, 0));
        Paragraph recordInEvidenceBook = new Paragraph("Poz. z książki ewidencji", font(12, 0));
        Paragraph numberOfMagazines = new Paragraph("Magazynki", font(12, 0));
        Paragraph gunCertificateSerialNumber = new Paragraph("Numer świadectwa", font(12, 0));


        PdfPTable titleTable = new PdfPTable(pointColumnWidths);
        titleTable.setWidthPercentage(100);

        PdfPCell lpCell = new PdfPCell(lp);
        PdfPCell modelNameCell = new PdfPCell(modelName);
        PdfPCell caliberAndProductionYearCell = new PdfPCell(caliberAndProductionYear);
        PdfPCell serialNumberCell = new PdfPCell(serialNumber);
        PdfPCell recordInEvidenceBookCell = new PdfPCell(recordInEvidenceBook);
        PdfPCell numberOfMagazinesCell = new PdfPCell(numberOfMagazines);
        PdfPCell gunCertificateSerialNumberCell = new PdfPCell(gunCertificateSerialNumber);

        lpCell.setHorizontalAlignment(1);
        lpCell.setVerticalAlignment(1);
        modelNameCell.setHorizontalAlignment(1);
        modelNameCell.setVerticalAlignment(1);
        caliberAndProductionYearCell.setHorizontalAlignment(1);
        caliberAndProductionYearCell.setVerticalAlignment(1);
        serialNumberCell.setHorizontalAlignment(1);
        serialNumberCell.setVerticalAlignment(1);
        recordInEvidenceBookCell.setHorizontalAlignment(1);
        recordInEvidenceBookCell.setVerticalAlignment(1);
        numberOfMagazinesCell.setHorizontalAlignment(1);
        numberOfMagazinesCell.setVerticalAlignment(1);
        gunCertificateSerialNumberCell.setHorizontalAlignment(1);
        gunCertificateSerialNumberCell.setVerticalAlignment(1);


        titleTable.addCell(lpCell);
        titleTable.addCell(modelNameCell);
        titleTable.addCell(caliberAndProductionYearCell);
        titleTable.addCell(serialNumberCell);
        titleTable.addCell(recordInEvidenceBookCell);
        titleTable.addCell(numberOfMagazinesCell);
        titleTable.addCell(gunCertificateSerialNumberCell);

        document.add(titleTable);

        List<GunEntity> collect1 = new ArrayList<>(gunRepository.findAllById(guns));

        collect1 = collect1.stream().sorted(Comparator.comparing(GunEntity::getCaliber).thenComparing(GunEntity::getModelName)).collect(Collectors.toList());

        for (int j = 0; j < collect1.size(); j++) {

            GunEntity gun = collect1.get(j);

            PdfPTable gunTable = new PdfPTable(pointColumnWidths);
            gunTable.setWidthPercentage(100);

            Paragraph lpGun = new Paragraph(String.valueOf(j + 1), font(12, 0));
            Paragraph modelNameGun = new Paragraph(gun.getModelName(), font(12, 0));
            Paragraph caliberAndProductionYearGun;
            if (gun.getProductionYear() != null && !gun.getProductionYear().isEmpty()) {
                caliberAndProductionYearGun = new Paragraph(gun.getCaliber() + "\nrok " + gun.getProductionYear(), font(12, 0));

            } else {
                caliberAndProductionYearGun = new Paragraph(gun.getCaliber(), font(12, 0));
            }
            Paragraph serialNumberGun = new Paragraph(gun.getSerialNumber(), font(12, 0));
            Paragraph recordInEvidenceBookGun = new Paragraph(gun.getRecordInEvidenceBook(), font(12, 0));
            Paragraph numberOfMagazinesGun = new Paragraph(gun.getNumberOfMagazines(), font(12, 0));
            Paragraph gunCertificateSerialNumberGun = new Paragraph(gun.getGunCertificateSerialNumber(), font(12, 0));

            PdfPCell lpGunCell = new PdfPCell(lpGun);
            PdfPCell modelNameGunCell = new PdfPCell(modelNameGun);
            PdfPCell caliberAndProductionYearGunCell = new PdfPCell(caliberAndProductionYearGun);
            PdfPCell serialNumberGunCell = new PdfPCell(serialNumberGun);
            PdfPCell recordInEvidenceBookGunCell = new PdfPCell(recordInEvidenceBookGun);
            PdfPCell numberOfMagazinesGunCell = new PdfPCell(numberOfMagazinesGun);
            PdfPCell gunCertificateSerialNumberGunCell = new PdfPCell(gunCertificateSerialNumberGun);

            lpGunCell.setHorizontalAlignment(1);
            lpGunCell.setVerticalAlignment(1);
            modelNameGunCell.setHorizontalAlignment(1);
            modelNameGunCell.setVerticalAlignment(1);
            caliberAndProductionYearGunCell.setHorizontalAlignment(1);
            caliberAndProductionYearGunCell.setVerticalAlignment(1);
            serialNumberGunCell.setHorizontalAlignment(1);
            serialNumberGunCell.setVerticalAlignment(1);
            recordInEvidenceBookGunCell.setHorizontalAlignment(1);
            recordInEvidenceBookGunCell.setVerticalAlignment(1);
            numberOfMagazinesGunCell.setHorizontalAlignment(1);
            numberOfMagazinesGunCell.setVerticalAlignment(1);
            gunCertificateSerialNumberGunCell.setHorizontalAlignment(1);
            gunCertificateSerialNumberGunCell.setVerticalAlignment(1);

            gunTable.addCell(lpGunCell);
            gunTable.addCell(modelNameGunCell);
            gunTable.addCell(caliberAndProductionYearGunCell);
            gunTable.addCell(serialNumberGunCell);
            gunTable.addCell(recordInEvidenceBookGunCell);
            gunTable.addCell(numberOfMagazinesGunCell);
            gunTable.addCell(gunCertificateSerialNumberGunCell);

            document.add(gunTable);


        }

        document.close();


        byte[] data = convertToByteArray(fileName);
        FilesModel filesModel = FilesModel.builder()
                .name(fileName)
                .data(data)
                .type(String.valueOf(MediaType.APPLICATION_PDF))
                .build();

        FilesEntity filesEntity =
                createFileEntity(filesModel);

        File file = new File(fileName);

        file.delete();
        return filesEntity;
    }

    private String getSex(String pesel) {
        int i = pesel.charAt(10);
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
        filesRepository.deleteById(filesEntity.getUuid());

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


    static class PageStamper extends PdfPageEventHelper {
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
