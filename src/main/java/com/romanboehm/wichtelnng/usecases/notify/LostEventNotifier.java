package com.romanboehm.wichtelnng.usecases.notify;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class LostEventNotifier {

    private final Logger log = LoggerFactory.getLogger(LostEventNotifier.class);

    private final LostEventMailCreator mailCreator;
    private final JavaMailSender mailSender;

    LostEventNotifier(LostEventMailCreator mailCreator, JavaMailSender mailSender) {
        this.mailCreator = mailCreator;
        this.mailSender = mailSender;
    }

    @Async
    void send(LostEventMailDto event) {
        try {
            MimeMessage message = mailCreator.createMessage(event);
            mailSender.send(message);
            log.info("Sent mail to inform host about empty event: {}", event);
        }
        catch (Exception e) {
            log.error("Failed to send mail to inform host about empty event: {}", event, e);
        }
    }
}
