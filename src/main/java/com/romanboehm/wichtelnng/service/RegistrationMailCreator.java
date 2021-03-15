package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.exception.WichtelnMailCreationException;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class RegistrationMailCreator {

    private final String domain;
    private final String from;
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public RegistrationMailCreator(
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


    public MimeMessage createMessage(ParticipantRegistration registration) throws WichtelnMailCreationException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
            message.setSubject(String.format("You have registered to wichtel at '%s'", registration.getTitle()));
            message.setFrom(from);
            message.setTo(registration.getParticipantEmail());

            Context ctx = new Context();
            ctx.setVariable("registration", registration);
            ctx.setVariable("domain", domain);
            String textContent = templateEngine.process("registrationmail.txt", ctx);
            message.setText(textContent);

            log.debug("Created mail for {}", registration);
            return mimeMessage;
        } catch (MessagingException e) {
            log.error("Failed to create mail for {}", registration, e);
            // Re-throw as custom `RuntimeException` to be handled by upstream by `ErrorController`
            throw new WichtelnMailCreationException();
        }
    }
}
