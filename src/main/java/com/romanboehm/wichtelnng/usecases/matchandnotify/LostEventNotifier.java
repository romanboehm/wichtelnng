package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Component
public class LostEventNotifier {

    private final LostEventMailCreator mailCreator;
    private final JavaMailSender mailSender;

    public void send(Event event) {
        MimeMessage message = mailCreator.createMessage(event);
        try {
            mailSender.send(message);
            log.info("Sent mail to inform about empty event: {}", event);
        } catch (Exception e) {
            log.error("Failed to send mail informing about empty event: {}", event, e);
        }
    }
}
