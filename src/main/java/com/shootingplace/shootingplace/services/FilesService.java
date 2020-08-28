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
    static BaseFont czcionka;


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

        String fileName = "Karta_Członkowska_" + memberEntity.getFirstName() + "_" + memberEntity.getSecondName()+".pdf";

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

        Paragraph p = new Paragraph(Element.ALIGN_CENTER, "KARTA CZŁONKOWSKA", new Font(czcionka, 20));

        document.add(p);

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
