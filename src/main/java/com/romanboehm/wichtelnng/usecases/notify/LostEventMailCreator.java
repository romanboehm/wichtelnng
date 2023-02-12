package com.romanboehm.wichtelnng.usecases.notify;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
class LostEventMailCreator {

    private final Logger log = LoggerFactory.getLogger(LostEventMailCreator.class);

    private final String domain;
    private final String from;
    private final JavaMailSender mailSender;

    LostEventMailCreator(
                         @Value("${com.romanboehm.wichtelnng.domain}") String domain,
                         @Value("${com.romanboehm.wichtelnng.mail.from}") String from,
                         JavaMailSender mailSender) {
        this.domain = domain;
        this.from = from;
        this.mailSender = mailSender;
    }

    MimeMessage createMessage(LostMailEvent event) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
        message.setSubject(format("Unfortunately, nobody has registered for '%s'", event.title()));
        message.setFrom(from);
        message.setTo(event.recipientEmail());

        message.setText(format(
                "Hey %s,%nUnfortunately nobody has registered to wichtel at '%s'.%nTry creating a new event: %s!%nThis mail was generated using %s",
                event.recipientName(),
                event.title(),
                domain,
                domain));

        log.debug("Created mail to inform about empty event: {}", event);
        return mimeMessage;
    }
}
