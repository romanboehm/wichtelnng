package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrationMailSender {

    private final AsyncJavaMailSender asyncJavaMailSender;
    private final RegistrationMailCreator registrationMailCreator;

    public void send(ParticipantRegistration participantRegistration) {
        MimeMessage message = registrationMailCreator.createMessage(participantRegistration);
        try {
            asyncJavaMailSender.send(message).get(30, TimeUnit.SECONDS);
            log.info("Sent mail for {}", participantRegistration);
        } catch (InterruptedException | ExecutionException | TimeoutException | RuntimeException e) {
            log.error("Failed to send mail for {}", participantRegistration, e);
        }
    }
}
