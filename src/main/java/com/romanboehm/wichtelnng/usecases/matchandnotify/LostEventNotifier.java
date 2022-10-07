package com.romanboehm.wichtelnng.usecases.matchandnotify;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Component
class LostEventNotifier {

    private final LostEventMailCreator mailCreator;
    private final JavaMailSender mailSender;

    void send(LostMailEvent event) {
        try {
            MimeMessage message = mailCreator.createMessage(event);
            mailSender.send(message);
            log.info("Sent mail to inform about empty event: {}", event);
        } catch (Exception e) {
            log.error("Failed to send mail informing about empty event: {}", event, e);
        }
    }
}
