package com.romanboehm.wichtelnng.usecases.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class MailToHostForCancelledEventSender {

    private final Logger log = LoggerFactory.getLogger(MailToHostForCancelledEventSender.class);

    private final MailToHostForCancelledEventComposer mailComposer;
    private final JavaMailSender mailSender;

    MailToHostForCancelledEventSender(MailToHostForCancelledEventComposer mailComposer, JavaMailSender mailSender) {
        this.mailComposer = mailComposer;
        this.mailSender = mailSender;
    }

    @Async
    void send(MailToHostDataForCancelledEvent mailData) {
        try {
            var message = mailComposer.createMessage(mailData);
            mailSender.send(message);
            log.info("Sent mail to inform host about cancelled event: {}", mailData);
        }
        catch (Exception e) {
            log.error("Failed to send mail to inform host about cancelled event: {}", mailData, e);
        }
    }
}
