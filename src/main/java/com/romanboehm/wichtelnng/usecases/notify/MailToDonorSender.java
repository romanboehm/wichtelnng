package com.romanboehm.wichtelnng.usecases.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class MailToDonorSender {

    private final Logger log = LoggerFactory.getLogger(MailToDonorSender.class);

    private final MailToDonorComposer mailComposer;
    private final JavaMailSender mailSender;

    MailToDonorSender(MailToDonorComposer mailComposer, JavaMailSender mailSender) {
        this.mailComposer = mailComposer;
        this.mailSender = mailSender;
    }

    @Async
    void send(List<MailToDonorData> mailToDonorDataList) {
        for (var mailToDonorData : mailToDonorDataList) {
            try {
                var message = mailComposer.createMessage(mailToDonorData);
                mailSender.send(message);
                log.info("Sent mail for {}", mailToDonorData);
            }
            catch (Exception e) {
                log.error("Failed to send mail for {}", mailToDonorData, e);
            }
        }
    }
}
