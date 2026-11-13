package com.kodat.complexhotelsystem.interfaces.IEmail;

import java.util.Map;

public interface IEmailSender {
    void sendSimpleEmail(
            String subject,
            String receiver,
            String content
    );

    void sendHtmlEmail(
            String subject,
            String receiver,
            String content
    );

    String renderEmail(
            String template,
            Map<String, Object> variables
    );
}
