package com.romanboehm.wichtelnng.usecases.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class MailToParticipantsForCancelledEventSender {

    private final Logger log = LoggerFactory.getLogger(MailToParticipantsForCancelledEventSender.class);

    private final MailToParticipantForCancelledEventComposer mailComposer;
    private final JavaMailSender mailSender;

    MailToParticipantsForCancelledEventSender(MailToParticipantForCancelledEventComposer mailComposer, JavaMailSender mailSender) {
        this.mailComposer = mailComposer;
        this.mailSender = mailSender;
    }

    @Async
    void send(List<MailToParticipantDataForCancelledEvent> mailDataList) {
        for (var mailData : mailDataList) {
            try {
                var message = mailComposer.createMessage(mailData);
                mailSender.send(message);
                log.info("Sent mail to inform participant about cancelled event: {}", mailData);
            }
            catch (Exception e) {
                log.error("Failed to send mail to inform participant about cancelled event: {}", mailData, e);
            }
        }
    }
}
