package com.kodat.complexhotelsystem;

import com.kodat.complexhotelsystem.Email.EmailSender;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {
    @Mock JavaMailSender mailSender;
    @Mock SpringTemplateEngine renderEngine;

    private EmailSender emailSender;

    @BeforeEach
    void setUp() {
        emailSender = new EmailSender(mailSender, renderEngine);
    }

    @Test
    void sendsSimpleEmailWithExpectedHeadersAndBody() {
        emailSender.sendSimpleEmail("Subject", "guest@example.test", "plain body");

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(message.capture());
        assertEquals("Subject", message.getValue().getSubject());
        assertEquals("guest@example.test", message.getValue().getTo()[0]);
        assertEquals("noreply@kodat.cz", message.getValue().getFrom());
        assertEquals("plain body", message.getValue().getText());
    }

    @Test
    void sendsHtmlEmailAsUtf8MimeMessage() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSender.sendHtmlEmail("Welcome", "guest@example.test", "<p>Welcome</p>");

        verify(mailSender).send(mimeMessage);
        assertEquals("Welcome", mimeMessage.getSubject());
        assertEquals("guest@example.test", mimeMessage.getAllRecipients()[0].toString());
        assertEquals("noreply@kodat.cz", mimeMessage.getFrom()[0].toString());
    }

    @Test
    void rendersTemplateWithProvidedVariables() {
        when(renderEngine.process(eq("emails/reset"), any(Context.class))).thenReturn("rendered");

        String rendered = emailSender.renderEmail("emails/reset", Map.of("resetCode", "123456"));

        assertEquals("rendered", rendered);
        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(renderEngine).process(eq("emails/reset"), context.capture());
        assertEquals("123456", context.getValue().getVariable("resetCode"));
    }
}
