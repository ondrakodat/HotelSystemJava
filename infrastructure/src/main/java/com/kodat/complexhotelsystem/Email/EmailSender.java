package com.kodat.complexhotelsystem.Email;

import com.kodat.complexhotelsystem.interfaces.IEmail.IEmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@AllArgsConstructor
@Service
public class EmailSender implements IEmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine renderEngine;

    @Override
    public void sendSimpleEmail(String subject, String receiver, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(content);
        message.setTo(receiver);
        message.setFrom("noreply@kodat.cz");

        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String subject, String receiver, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom("noreply@kodat.cz");
            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String renderEmail(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return renderEngine.process("/Templates" + template, context);
    }
}
