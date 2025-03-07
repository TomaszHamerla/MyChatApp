package com.example.chatapp.service;

import com.example.chatapp.model.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final LogService logService;

    @Async
    public void sendEmail(
            String to, EmailTemplateName emailTemplate, String activationCode, String subject
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", to);
        properties.put("activation_code", activationCode);
        Context context = new Context();
        context.setVariables(properties);
        String htmlContent = templateEngine.process(emailTemplate.getName(), context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        logService.logInfo("Sending email");
        mailSender.send(message);
    }
}
