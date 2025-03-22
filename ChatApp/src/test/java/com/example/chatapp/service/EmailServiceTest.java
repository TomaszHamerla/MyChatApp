package com.example.chatapp.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.example.chatapp.model.email.EmailTemplateName.ACTIVATE_ACCOUNT;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void shouldSendRealEmail() throws MessagingException {
        // given
        String to = "tomaszhamerla15@gmail.com";
        String activationCode = "123456";
        String subject = "Testowy e-mail";

        // when + then
        emailService.sendEmail(
                to, ACTIVATE_ACCOUNT, activationCode, subject, "http://localhost:8080/activate-account"
        );
    }
}
