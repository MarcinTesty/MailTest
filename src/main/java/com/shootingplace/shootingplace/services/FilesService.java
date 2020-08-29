package com.shootingplace.shootingplace.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.shootingplace.shootingplace.domain.entities.FilesEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.FilesModel;
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
    private final FilesRepository filesRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public FilesService(MemberRepository memberRepository, FilesRepository filesRepository) {
        this.memberRepository = memberRepository;
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


    void contributionConfirm(UUID memberUUID) throws DocumentException, IOException {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        LocalDate contribution = memberEntity.getContribution().getContribution();
        String fileName = "Składka_" + memberEntity.getFirstName() + "_" + memberEntity.getSecondName() + "_" + LocalDate.now() + ".pdf";

        Document document = new Document(PageSize.A5.rotate());
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

        String status;
        if (getSex(memberEntity.getPesel()).equals("Pani")) {
            status = "opłaciła";
        } else {
            status = "opłacił";
        }

        Paragraph p = new Paragraph(Element.ALIGN_JUSTIFIED, "\nPotwierdzenie opłacenia składki członkowskiej\n".toUpperCase(), new Font(czcionka, 14));
        Paragraph p1 = new Paragraph(getSex(memberEntity.getPesel()) + " " + memberEntity.getFirstName() + " " + memberEntity.getSecondName() + " nr legitymacji " + memberEntity.getLegitimationNumber(), new Font(czcionka));
        Paragraph p2 = new Paragraph("Dnia " + LocalDate.now() + " " + status + " składkę członkowską\n", new Font(czcionka));
        Paragraph p3 = new Paragraph("Składka ważna do : " + contribution + "\n", new Font(czcionka));
        Paragraph p4 = new Paragraph("Najpóźniejszy termin opłacenia kolejnej składki to : " + contribution.plusMonths(9), new Font(czcionka));
        Paragraph p5 = new Paragraph("Tutaj można dodać jakieś przypomnienie w stylu, że jeśli składka nie będzie opłacona to " +
                "coś tam coś tam\n\n\n\n\n\n", new Font(czcionka));
        Paragraph p6 = new Paragraph("Podpis Klubowicza" +
                "                                                                      " +
                " Podpis Osoby przyjmującej składkę", new Font(czcionka));

        String imFile = "logo_midi.jpg";

        document.add(p);
        document.add(p1);
        document.add(p2);
        document.add(p3);
        document.add(p4);
        document.add(p5);
        document.add(p6);
        document.close();

        byte[] data = convertToByteArray(fileName);

        FilesEntity filesEntity = memberEntity.getContributionFile();
        filesEntity.setName(fileName);
        filesEntity.setType(String.valueOf(MediaType.APPLICATION_PDF));
        filesEntity.setData(data);
        File file = new File(fileName);
        MultipartFile multipartFile = new MockMultipartFile(fileName,
                file.getName(), filesEntity.getType(), filesEntity.getData());
        memberEntity.setContributionFile(saveFile(multipartFile));
        memberRepository.saveAndFlush(memberEntity);
        file.delete();
    }

    void personalCardFile(UUID memberUUID) throws IOException, DocumentException {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        String fileName = "Karta_Członkowska_" + memberEntity.getFirstName() + "_" + memberEntity.getSecondName() + ".pdf";

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

        Paragraph p = new Paragraph("KARTA CZŁONKOWSKA\n", new Font(czcionka, 20));
        p.setIndentationLeft(150);
        String status;
        if (memberEntity.getAdult()) {
            status = "Gr. Powszechna";
        } else {
            status = "Gr. Młodzieżowa";
        }
        Paragraph p1 = new Paragraph("            Nr Leg. " + memberEntity.getLegitimationNumber() + " " + status, new Font(czcionka, 14));
        p.add(p1);
        Paragraph p2 = new Paragraph("Imię i Nazwisko : ", new Font(czcionka, 14));
        p2.setAlignment(Element.ALIGN_JUSTIFIED);
        p2.add(memberEntity.getFirstName() + " " + memberEntity.getSecondName());
        Paragraph p3 = new Paragraph("", new Font(czcionka, 14));
        p3.add("PESEL : " + memberEntity.getPesel());
        p3.add("\n");
        p3.add("Numer dowodu : " + memberEntity.getIDCard());
        Paragraph p4 = new Paragraph("dane teleadresowe".toUpperCase(), new Font(czcionka, 16));
        p4.setIndentationLeft(175);
        Paragraph p5 = new Paragraph("", new Font(czcionka, 14));
        if (memberEntity.getEmail() != null) {
            p5.add("Email : " + memberEntity.getEmail());
        } else {
            p5.add("Email : Nie podano");
        }
        p5.add("\n");
        p5.add("Numer telefonu : " + memberEntity.getPhoneNumber());
        Paragraph p6 = new Paragraph("Adres Zamieszkania".toUpperCase(), new Font(czcionka, 14));
        p6.setIndentationLeft(50);
        Paragraph p7 = new Paragraph("", new Font(czcionka, 14));
        if (memberEntity.getAddress().getPostOfficeCity() != null) {
            p7.add("Miasto : " + memberEntity.getAddress().getPostOfficeCity());
        } else {
            p7.add("Miasto : ");
        }
        p7.add("\n");
        if (memberEntity.getAddress().getZipCode() != null) {
            p7.add("Kod pocztowy : " + memberEntity.getAddress().getZipCode());
        } else {
            p7.add("Kod pocztowy : ");
        }
        p7.add("\n");
        if (memberEntity.getAddress().getStreet() != null) {
            p7.add("Ulica : " + memberEntity.getAddress().getStreet());
            if (memberEntity.getAddress().getStreetNumber() != null) {
                p7.add(" " + memberEntity.getAddress().getStreetNumber());
            } else {
                p7.add(" ");
            }
        } else {
            p7.add("Ulica : ");
        }
        p7.add("\n");
        if (memberEntity.getAddress().getFlatNumber() != null) {
            p7.add("Numer Mieszkania : " + memberEntity.getAddress().getFlatNumber());
        } else {
            p7.add("Numer Mieszkania : ");
        }
        Paragraph p8 = new Paragraph("RODO", new Font(czcionka, 20));
        Paragraph p9 = new Paragraph("", new Font(czcionka, 10));
        p9.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin massa mauris, scelerisque et finibus eu, commodo et augue. In ornare eleifend leo, sed euismod nisi molestie in. Vivamus viverra laoreet velit sed efficitur. Nullam varius purus in convallis tincidunt. Integer vulputate cursus iaculis. Fusce a vehicula lorem. Suspendisse ut lacus et erat ultrices dictum sed nec nisl.\n" +
                "\n" +
                "Vestibulum elit enim, hendrerit sed posuere in, ultricies sed mi. Donec non purus nulla. Nulla condimentum lacinia laoreet. Praesent ultricies eu enim vel faucibus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Proin eleifend libero sed volutpat convallis. Aliquam sapien urna, porta eu bibendum eget, gravida in metus. Fusce lobortis mi eget dolor suscipit, in elementum arcu ultricies. Ut rhoncus ante ut mauris commodo, ac cursus elit rutrum. Integer tincidunt vulputate sem, pellentesque congue lacus rutrum eget. In at nisl ac eros molestie gravida ut in sem. Nam non ex suscipit, tempus ex sit amet, porttitor lectus.\n" +
                "\n" +
                "Aenean consectetur finibus sapien, consectetur tincidunt augue euismod sed. Sed ullamcorper varius bibendum. Aliquam magna odio, ultricies a purus sed, iaculis ultricies dui. Maecenas ac vestibulum erat. Nullam facilisis, ante in convallis volutpat, purus enim ullamcorper purus, non porttitor enim elit aliquam turpis. Vivamus eget justo lacus. Proin et consectetur lectus. Suspendisse faucibus odio magna, a tempor diam volutpat non. Pellentesque diam libero, ullamcorper facilisis euismod maximus, ornare at felis. Nullam aliquet leo tortor, ut imperdiet ipsum aliquet a. Donec sed mauris venenatis, rhoncus elit ac, condimentum ligula.\n" +
                "\n" +
                "Nam mi augue, vulputate eu libero non, vulputate dignissim neque. Vivamus suscipit urna at urna vestibulum malesuada. Nulla facilisi. Nam vel felis ut dui convallis placerat molestie consequat sem. Vestibulum at mi condimentum, rutrum est at, convallis nisl. Cras eu euismod tortor. Quisque ut sem nec mauris lacinia posuere. Donec interdum euismod augue. Aenean eu suscipit nisl. Suspendisse sed finibus ex, at fermentum eros. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Vestibulum pellentesque vestibulum libero vitae consequat. Proin sit amet condimentum enim. Nulla facilisi.\n" +
                "\n" +
                "Proin vel blandit nisl. Donec sed felis interdum, accumsan dolor sed, fringilla dolor. Sed aliquam feugiat velit eu venenatis. Vivamus sed est fermentum, fermentum magna id, porta lectus. Nunc vestibulum egestas quam ut tincidunt. Vivamus consectetur, enim nec venenatis tempus, tellus nisi facilisis diam, ac interdum risus nisi et mauris. Suspendisse elementum sed dolor sed congue. Nunc volutpat in augue at scelerisque. Pellentesque at ultricies est, vitae dapibus purus.");

//  Imię nazwisko
//  PESEL
//  Numer Dowodu
//  Numer Legitymacji
//
//
//
//
//  RODO
        document.add(p);
        document.add(p2);
        document.add(p3);
        document.add(p4);
        document.add(p5);
        document.add(p6);
        document.add(p7);
        document.add(p8);
        document.add(p9);

        document.close();

        byte[] data = convertToByteArray(fileName);

        FilesEntity filesEntity = memberEntity.getPersonalCardFile();
        filesEntity.setName(fileName);
        filesEntity.setType(String.valueOf(MediaType.APPLICATION_PDF));
        filesEntity.setData(data);
        File file = new File(fileName);
        MultipartFile multipartFile = new MockMultipartFile(fileName,
                file.getName(), filesEntity.getType(), filesEntity.getData());
        memberEntity.setPersonalCardFile(saveFile(multipartFile));
        memberRepository.saveAndFlush(memberEntity);
        file.delete();

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

    private FilesEntity saveFile(MultipartFile file) {
        String docName = file.getOriginalFilename();
        try {
            FilesModel doc = new FilesModel(docName, file.getContentType(), file.getBytes());
            return filesRepository.saveAndFlush(Mapping.map(doc));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] convertToByteArray(String path) throws IOException {
        File file = new File(path);
        return Files.readAllBytes(file.toPath());

    }

}
