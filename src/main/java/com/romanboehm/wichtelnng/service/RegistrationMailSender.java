package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrationMailSender {

    private final JavaMailSender javaMailSender;
    private final RegistrationMailCreator registrationMailCreator;

    public void send(ParticipantRegistration participantRegistration) {
        MimeMessage message = registrationMailCreator.createMessage(participantRegistration);
        try {
            javaMailSender.send(message);
            log.info("Sent mail for {}", participantRegistration);
        } catch (Exception e) {
            log.error("Failed to send mail for {}", participantRegistration, e);
        }
    }
}
