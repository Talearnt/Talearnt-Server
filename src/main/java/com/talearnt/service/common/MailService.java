package com.talearnt.service.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendFindPasswordHtmlMessage(String toEmail, String title, String content) throws MessagingException {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage,"UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content,true);

        mailSender.send(mailMessage);

    }
}
