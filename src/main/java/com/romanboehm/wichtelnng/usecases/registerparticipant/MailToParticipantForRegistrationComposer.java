package com.romanboehm.wichtelnng.usecases.registerparticipant;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
class MailToParticipantForRegistrationComposer {

    private final Logger log = LoggerFactory.getLogger(MailToParticipantForRegistrationComposer.class);

    private final String domain;
    private final String from;
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    MailToParticipantForRegistrationComposer(
                                             @Value("${com.romanboehm.wichtelnng.domain}") String domain,
                                             @Value("${com.romanboehm.wichtelnng.mail.from}") String from,
                                             TemplateEngine templateEngine,
                                             JavaMailSender mailSender) {
        this.domain = domain;
        this.from = from;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    MimeMessage createMessage(MailToParticipantDataForRegistration mailToParticipantDataForRegistration) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
            message.setSubject(String.format("You have registered to wichtel at '%s'", mailToParticipantDataForRegistration.title()));
            message.setFrom(from);
            message.setTo(mailToParticipantDataForRegistration.participantEmail());

            var ctx = new Context();
            ctx.setVariable("data", mailToParticipantDataForRegistration);
            ctx.setVariable("domain", domain);
            var textContent = templateEngine.process("registrationmail.txt", ctx);
            message.setText(textContent);

            log.debug("Created mail for {}", mailToParticipantDataForRegistration);
            return mimeMessage;
        }
        catch (MessagingException e) {
            log.error("Failed to create mail for {}", mailToParticipantDataForRegistration, e);
            // Re-throw as `RuntimeException` to be handled by upstream by `ErrorController`
            throw new RuntimeException("Failed to create registration mail");
        }
    }
}
