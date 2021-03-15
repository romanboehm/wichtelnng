package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MatchMailSender {

    private final MatchMailCreator matchMailCreator;
    private final AsyncJavaMailSender asyncJavaMailSender;
    
    public void send(Event event, List<Match> matches) {
        for (Match match : matches) {
            MimeMessage message = matchMailCreator.createMessage(event, match);
            try {
                asyncJavaMailSender.send(message).get(30, TimeUnit.SECONDS);
                log.info("Sent mail for {}", match);
            } catch (InterruptedException | ExecutionException | TimeoutException | RuntimeException e) {
                log.error("Failed to send mail for {}", match, e);
            }
        }
    }
}
