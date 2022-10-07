package com.romanboehm.wichtelnng.usecases.matchandnotify;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
class MatchNotifier {

    private final MatchMailCreator mailCreator;
    private final JavaMailSender mailSender;

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
