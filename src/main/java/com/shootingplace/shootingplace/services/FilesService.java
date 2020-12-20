package com.shootingplace.shootingplace.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.FilesEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.FilesModel;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import com.shootingplace.shootingplace.repositories.FilesRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class FilesService {
    private static BaseFont czcionka;


    private final MemberRepository memberRepository;
    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final FilesRepository filesRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public FilesService(MemberRepository memberRepository, AmmoEvidenceRepository ammoEvidenceRepository, FilesRepository filesRepository) {
        this.memberRepository = memberRepository;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
        this.filesRepository = filesRepository;
    }

    void createContributionFileEntity(UUID memberUUID, FilesModel filesModel) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getContributionFile() != null) {
            LOG.error("nie można już dodać pola z plikiem");
        }
        FilesEntity filesEntity = Mapping.map(filesModel);
        filesRepository.saveAndFlush(filesEntity);
        memberEntity.setContributionFile(filesEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("pole z plikiem Potwierdzenia Składki zostało zapisane");

    }

    void createPersonalCardFileEntity(UUID memberUUID, FilesModel filesModel) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getPersonalCardFile() != null) {
            LOG.error("nie można już dodać pola z plikiem");
        }
        FilesEntity filesEntity = Mapping.map(filesModel);
        filesRepository.saveAndFlush(filesEntity);
        memberEntity.setPersonalCardFile(filesEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("pole z plikiem Karty Personalnej zostało zapisane");

    }

    void createAmmoListFileEntity(UUID ammoListUUID, FilesModel filesModel) {
        AmmoEvidenceEntity ammoEvidenceEntity = ammoEvidenceRepository.findById(ammoListUUID).orElseThrow(EntityNotFoundException::new);
        if (ammoEvidenceEntity.getFile() != null) {
            LOG.error("nie można już dodać pola z plikiem");
        }
        FilesEntity filesEntity = Mapping.map(filesModel);
        filesRepository.saveAndFlush(filesEntity);
        ammoEvidenceEntity.setFile(filesEntity);
        ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
        LOG.info("pole z plikiem Karty Personalnej zostało zapisane");

    }


    public void contributionConfirm(UUID memberUUID) throws DocumentException, IOException {
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
//        Phrase p11 = new Phrase(memberEntity.getSecondName() + " " + memberEntity.getFirstName() + " dnia : " + memberEntity.getContribution().getPaymentDay() + " " + status + " półroczną składkę członkowską w wysokości " + contributionLevel + " PLN.", new Font(czcionka, 11));
        Paragraph p12 = new Paragraph("\n\n\n\n\nTermin opłacenia kolejnej składki : ", new Font(czcionka, 11));
//        Paragraph p13 = new Paragraph("\n" + (contribution.plusMonths(3)), new Font(czcionka, 11, Font.BOLD));
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
//        p10.add(p11);
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
//        document.add(p13);
        document.add(p14);
        document.add(p16);
        document.add(p19);

        document.close();

        byte[] data = convertToByteArray(fileName);

        FilesEntity filesEntity = memberEntity.getContributionFile();
        filesEntity.setName(fileName);
        filesEntity.setType(String.valueOf(MediaType.APPLICATION_PDF));
        filesEntity.setData(data);
        File file = new File(fileName);
        MultipartFile multipartFile = new MockMultipartFile(fileName,
                file.getName(), filesEntity.getType(), filesEntity.getData());
        memberEntity.setContributionFile(saveContributionFile(multipartFile, memberEntity.getUuid()));
        memberRepository.saveAndFlush(memberEntity);
        file.delete();
    }

    public void personalCardFile(UUID memberUUID) throws IOException, DocumentException {
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

        FilesEntity filesEntity = memberEntity.getPersonalCardFile();
        filesEntity.setName(fileName);
        filesEntity.setType(String.valueOf(MediaType.APPLICATION_PDF));
        filesEntity.setData(data);
        File file = new File(fileName);
        MultipartFile multipartFile = new MockMultipartFile(fileName,
                file.getName(), filesEntity.getType(), filesEntity.getData());
        memberEntity.setPersonalCardFile(savePersonalCardFile(multipartFile, memberEntity.getUuid()));
        memberRepository.saveAndFlush(memberEntity);
        file.delete();

    }

    void createAmmunitionListDocument(UUID ammoEvidenceUUID) throws IOException, DocumentException {

        AmmoEvidenceEntity ammoEvidenceEntity = ammoEvidenceRepository.findById(ammoEvidenceUUID).orElseThrow(EntityNotFoundException::new);

        String fileName = ammoEvidenceEntity.getLabel().concat(" " + ammoEvidenceEntity.getDate() + ".pdf");

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

        Paragraph p = new Paragraph("KLUB STRZELECKI „DZIESIĄTKA” LOK W ŁODZI\n", new Font(czcionka, 14, Font.BOLD));
        Paragraph p1 = new Paragraph("Lista rozliczenia amunicji\n", new Font(czcionka, 14, Font.ITALIC));
//        if (!ammoEvidenceEntity.getCaliberList().get(0).getMembers().isEmpty()) {
//
//            Phrase p2 = new Phrase(String.valueOf(ammoEvidenceEntity.getCaliberList().get(0).getMembers().get(0)), new Font(czcionka, 14, Font.BOLD));
//            p1.add(p2);
//
//        }


        p.setIndentationLeft(100);
        p1.add("\n");
        p1.setIndentationLeft(190);


        document.add(p);
        document.add(p1);

        document.close();


        byte[] data = convertToByteArray(fileName);

        FilesEntity filesEntity = ammoEvidenceEntity.getFile();
        filesEntity.setName(fileName);
        filesEntity.setType(String.valueOf(MediaType.APPLICATION_PDF));
        filesEntity.setData(data);
        File file = new File(fileName);
        MultipartFile multipartFile = new MockMultipartFile(fileName,
                file.getName(), filesEntity.getType(), filesEntity.getData());
        ammoEvidenceEntity.setFile(saveAmmunitionListFile(multipartFile, ammoEvidenceEntity.getUuid()));
        ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);

    }

    private String getSex(String pesel) {
        int i = (int) pesel.charAt(8);
        if (i % 2 == 1) {
            return "Pani";
        } else return "Pan";
    }

    public Optional<FilesEntity> getFile(UUID fileUUID) {
        return filesRepository.findById(fileUUID);
    }

    private FilesEntity saveContributionFile(MultipartFile file, UUID memberUUID) throws IOException {
        String docName = file.getOriginalFilename();
        FilesEntity filesEntity = memberRepository.findById(memberUUID).get().getContributionFile();
        filesEntity.setName(docName);
        filesEntity.setType(file.getContentType());
        filesEntity.setData(file.getBytes());
        return filesRepository.saveAndFlush(filesEntity);
    }

    private FilesEntity savePersonalCardFile(MultipartFile file, UUID memberUUID) throws IOException {
        String docName = file.getOriginalFilename();
        FilesEntity filesEntity = memberRepository.findById(memberUUID).get().getPersonalCardFile();
        filesEntity.setName(docName);
        filesEntity.setType(file.getContentType());
        filesEntity.setData(file.getBytes());
        return filesRepository.saveAndFlush(filesEntity);
    }

    private FilesEntity saveAmmunitionListFile(MultipartFile file, UUID ammoListUUID) throws IOException {
        String docName = file.getOriginalFilename();
        FilesEntity filesEntity = ammoEvidenceRepository.findById(ammoListUUID).get().getFile();
        filesEntity.setName(docName);
        filesEntity.setType(file.getContentType());
        filesEntity.setData(file.getBytes());
        return filesRepository.saveAndFlush(filesEntity);
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

}
