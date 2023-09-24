package com.romanboehm.wichtelnng.usecases.notify;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class LostParticipationNotifier {

    private final Logger log = LoggerFactory.getLogger(LostParticipationNotifier.class);

    private final LostParticipationMailCreator mailCreator;
    private final JavaMailSender mailSender;

    LostParticipationNotifier(LostParticipationMailCreator mailCreator, JavaMailSender mailSender) {
        this.mailCreator = mailCreator;
        this.mailSender = mailSender;
    }

    @Async
    void send(List<LostParticipationMailDto> events) {
        for (LostParticipationMailDto event : events) {
            try {
                MimeMessage message = mailCreator.createMessage(event);
                mailSender.send(message);
                log.info("Sent mail to inform participant about empty event: {}", event);
            }
            catch (Exception e) {
                log.error("Failed to send mail to inform participant about empty event: {}", event, e);
            }
        }
    }
}
