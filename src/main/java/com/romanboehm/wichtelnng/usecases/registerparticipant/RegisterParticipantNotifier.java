package com.romanboehm.wichtelnng.usecases.registerparticipant;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
class RegisterParticipantNotifier {

    private final Logger log = LoggerFactory.getLogger(RegisterParticipantNotifier.class);

    private final JavaMailSender mailSender;
    private final RegisterParticipantMailCreator mailCreator;

    RegisterParticipantNotifier(JavaMailSender mailSender, RegisterParticipantMailCreator mailCreator) {
        this.mailSender = mailSender;
        this.mailCreator = mailCreator;
    }

    void send(RegistrationMailEvent registrationMailEvent) {
        MimeMessage message = mailCreator.createMessage(registrationMailEvent);
        try {
            mailSender.send(message);
            log.info("Sent mail for {}", registrationMailEvent);
        } catch (Exception e) {
            log.error("Failed to send mail for {}", registrationMailEvent, e);
        }
    }
}
