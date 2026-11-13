package com.kodat.complexhotelsystem.Email;

import com.kodat.complexhotelsystem.interfaces.IEmail.IEmailSender;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

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

        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String subject, String receiver, String content) {

    }

    @Override
    public String renderEmail(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return renderEngine.process("/emails" + template, context);
    }
}
