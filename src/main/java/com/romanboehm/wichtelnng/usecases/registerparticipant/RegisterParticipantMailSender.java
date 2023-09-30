package com.romanboehm.wichtelnng.usecases.registerparticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class RegisterParticipantMailSender {

    private final Logger log = LoggerFactory.getLogger(RegisterParticipantMailSender.class);

    private final JavaMailSender mailSender;
    private final MailToParticipantForRegistrationComposer mailComposer;

    RegisterParticipantMailSender(JavaMailSender mailSender, MailToParticipantForRegistrationComposer mailComposer) {
        this.mailSender = mailSender;
        this.mailComposer = mailComposer;
    }

    @Async
    void send(MailToParticipantDataForRegistration mailToParticipantDataForRegistration) {
        var message = mailComposer.createMessage(mailToParticipantDataForRegistration);
        try {
            mailSender.send(message);
            log.info("Sent mail for {}", mailToParticipantDataForRegistration);
        }
        catch (Exception e) {
            log.error("Failed to send mail for {}", mailToParticipantDataForRegistration, e);
        }
    }
}
