package com.romanboehm.wichtelnng.usecases.registerparticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
class RegisterParticipantMailCreator {

    private final Logger log = LoggerFactory.getLogger(RegisterParticipantMailCreator.class);

    private final String domain;
    private final String from;
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    RegisterParticipantMailCreator(
            @Value("${com.romanboehm.wichtelnng.domain}") String domain,
            @Value("${com.romanboehm.wichtelnng.mail.from}") String from,
            TemplateEngine templateEngine,
            JavaMailSender mailSender
    ) {
        this.domain = domain;
        this.from = from;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }


    MimeMessage createMessage(RegisterParticipant registerParticipant) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
            message.setSubject(String.format("You have registered to wichtel at '%s'", registerParticipant.getTitle()));
            message.setFrom(from);
            message.setTo(registerParticipant.getParticipantEmail());

            Context ctx = new Context();
            ctx.setVariable("registerParticipant", registerParticipant);
            ctx.setVariable("domain", domain);
            String textContent = templateEngine.process("registrationmail.txt", ctx);
            message.setText(textContent);

            log.debug("Created mail for {}", registerParticipant);
            return mimeMessage;
        } catch (MessagingException e) {
            log.error("Failed to create mail for {}", registerParticipant, e);
            // Re-throw as `RuntimeException` to be handled by upstream by `ErrorController`
            throw new RuntimeException("Failed to create registration mail");
        }
    }
}
