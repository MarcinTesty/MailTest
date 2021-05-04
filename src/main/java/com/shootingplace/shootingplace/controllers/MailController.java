package com.shootingplace.shootingplace.controllers;


import com.shootingplace.shootingplace.services.MailWithAttachmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/testmail")
public class MailController {

    private final MailWithAttachmentService mailWithAttachmentService;


    public MailController(MailWithAttachmentService mailWithAttachmentService) {
        this.mailWithAttachmentService = mailWithAttachmentService;
    }

    @GetMapping
    public String sendTestMail() throws IOException, MessagingException {
        mailWithAttachmentService.sendMail(mailWithAttachmentService.getSession());
        return "Wyslano";
    }

}
