package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MatchMailSender {

    private final MatchMailCreator matchMailCreator;
    private final JavaMailSender javaMailSender;
    
    public void send(Event event, List<Match> matches) {
        for (Match match : matches) {
            MimeMessage message = matchMailCreator.createMessage(event, match);
            try {
                javaMailSender.send(message);
                log.info("Sent mail for {}", match);
            } catch (Exception e) {
                log.error("Failed to send mail for {}", match, e);
            }
        }
    }
}
