package com.romanboehm.wichtelnng.usecases.registerparticipant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegisterParticipantNotifier {

    private final JavaMailSender mailSender;
    private final RegisterParticipantMailCreator mailCreator;

    void send(RegisterParticipant registerParticipant) {
        MimeMessage message = mailCreator.createMessage(registerParticipant);
        try {
            mailSender.send(message);
            log.info("Sent mail for {}", registerParticipant);
        } catch (Exception e) {
            log.error("Failed to send mail for {}", registerParticipant, e);
        }
    }
}
