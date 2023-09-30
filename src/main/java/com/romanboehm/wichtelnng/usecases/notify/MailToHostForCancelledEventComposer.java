package com.romanboehm.wichtelnng.usecases.notify;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
class MailToHostForCancelledEventComposer {

    private final Logger log = LoggerFactory.getLogger(MailToHostForCancelledEventComposer.class);

    private final String domain;
    private final String from;
    private final JavaMailSender mailSender;

    MailToHostForCancelledEventComposer(
                                        @Value("${com.romanboehm.wichtelnng.domain}") String domain,
                                        @Value("${com.romanboehm.wichtelnng.mail.from}") String from,
                                        JavaMailSender mailSender) {
        this.domain = domain;
        this.from = from;
        this.mailSender = mailSender;
    }

    MimeMessage createMessage(MailToHostDataForCancelledEvent event) throws MessagingException {
        var mimeMessage = mailSender.createMimeMessage();
        var message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
        message.setSubject("Unfortunately, not enough people have registered for '%s'".formatted(event.title()));
        message.setFrom(from);
        message.setTo(event.recipientEmail());

        message.setText(
                "Hey %s,%nUnfortunately, not enough people have registered to wichtel at '%s'.%nTry creating a new event: %s!%nThis mail was generated using %s"
                        .formatted(
                                event.recipientName(),
                                event.title(),
                                domain,
                                domain));

        log.debug("Created mail to inform about empty event: {}", event);
        return mimeMessage;
    }
}
