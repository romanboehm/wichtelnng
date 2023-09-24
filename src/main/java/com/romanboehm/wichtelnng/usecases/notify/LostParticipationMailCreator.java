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
class LostParticipationMailCreator {

    private final Logger log = LoggerFactory.getLogger(LostParticipationMailCreator.class);

    private final String domain;
    private final String from;
    private final JavaMailSender mailSender;

    LostParticipationMailCreator(
                                 @Value("${com.romanboehm.wichtelnng.domain}") String domain,
                                 @Value("${com.romanboehm.wichtelnng.mail.from}") String from,
                                 JavaMailSender mailSender) {
        this.domain = domain;
        this.from = from;
        this.mailSender = mailSender;
    }

    MimeMessage createMessage(LostParticipationMailDto event) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
        message.setSubject("Unfortunately, not enough other people have registered for '%s'".formatted(event.title()));
        message.setFrom(from);
        message.setTo(event.recipientEmail());

        message.setText(
                "Hey %s,%nUnfortunately, not enough other people have registered to wichtel at '%s'.%nTry contacting the event's host if you have any questions: %s!%nThis mail was generated using %s"
                        .formatted(
                                event.recipientName(),
                                event.title(),
                                event.host().email(),
                                domain));

        log.debug("Created mail to inform about empty event: {}", event);
        return mimeMessage;
    }
}
