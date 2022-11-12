package com.romanboehm.wichtelnng.usecases.matchandnotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Component
class MatchNotifier {

    private final Logger log = LoggerFactory.getLogger(MatchNotifier.class);

    private final MatchMailCreator mailCreator;
    private final JavaMailSender mailSender;

    MatchNotifier(MatchMailCreator mailCreator, JavaMailSender mailSender) {
        this.mailCreator = mailCreator;
        this.mailSender = mailSender;
    }

    @Async
    void send(List<MatchMailEvent> matchMailEvents) {
        for (MatchMailEvent matchMailEvent : matchMailEvents) {
            try {
                MimeMessage message = mailCreator.createMessage(matchMailEvent);
                mailSender.send(message);
                log.info("Sent mail for {}", matchMailEvent);
            } catch (Exception e) {
                log.error("Failed to send mail for {}", matchMailEvent, e);
            }
        }
    }
}
