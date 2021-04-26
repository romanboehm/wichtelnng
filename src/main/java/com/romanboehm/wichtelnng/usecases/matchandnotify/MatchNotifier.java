package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MatchNotifier {

    private final MatchMailCreator mailCreator;
    private final JavaMailSender mailSender;
    
    void send(Event event, List<Match> matches) {
        for (Match match : matches) {
            MimeMessage message = mailCreator.createMessage(event, match);
            try {
                mailSender.send(message);
                log.info("Sent mail for {}", match);
            } catch (Exception e) {
                log.error("Failed to send mail for {}", match, e);
            }
        }
    }
}
