package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class RegistrationMailSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(RegistrationMailSender.class);

    private final AsyncJavaMailSender asyncJavaMailSender;
    private final RegistrationMailCreator registrationMailCreator;

    public RegistrationMailSender(
            AsyncJavaMailSender asyncJavaMailSender,
            RegistrationMailCreator registrationMailCreator
    ) {
        this.asyncJavaMailSender = asyncJavaMailSender;
        this.registrationMailCreator = registrationMailCreator;
    }

    public void send(ParticipantRegistration participantRegistration) {
        MimeMessage message = registrationMailCreator.createMessage(participantRegistration);
        try {
            asyncJavaMailSender.send(message).get(30, TimeUnit.SECONDS);
            LOGGER.info("Sent mail for {}", participantRegistration);
        } catch (InterruptedException | ExecutionException | TimeoutException | RuntimeException e) {
            LOGGER.error("Failed to send mail for {}", participantRegistration, e);
        }
    }
}
