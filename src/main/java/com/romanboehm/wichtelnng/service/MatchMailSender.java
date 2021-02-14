package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class MatchMailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchMailSender.class);

    private final MatchMailCreator matchMailCreator;
    private final AsyncJavaMailSender asyncJavaMailSender;

    public MatchMailSender(MatchMailCreator matchMailCreator, AsyncJavaMailSender asyncJavaMailSender) {
        this.matchMailCreator = matchMailCreator;
        this.asyncJavaMailSender = asyncJavaMailSender;
    }

    public void send(Event event, List<Match> matches) {
        for (Match match : matches) {
            MimeMessage message = matchMailCreator.createMessage(event, match);
            try {
                asyncJavaMailSender.send(message).get(30, TimeUnit.SECONDS);
                LOGGER.info("Sent mail for {}", match);
            } catch (InterruptedException | ExecutionException | TimeoutException | RuntimeException e) {
                LOGGER.error("Failed to send mail for {}", match, e);
            }
        }
    }
}
