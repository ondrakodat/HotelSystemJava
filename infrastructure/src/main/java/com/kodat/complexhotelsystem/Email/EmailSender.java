package com.kodat.complexhotelsystem.Email;

import com.kodat.complexhotelsystem.interfaces.IEmail.IEmailSender;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
public class EmailSender implements IEmailSender {
    private final JavaMailSender mailSender;

    @Override
    public void sendSimpleEmail(String subject, String receiver, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(content);
        message.setTo(receiver);

        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String subject, String receiver, String content) {

    }

    @Override
    public String renderEmail(String template, Map<String, Object> variables) {
        return "";
    }
}
