package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.exception.WichtelnMailCreationException;
import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
public class RegistrationMailCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationMailCreator.class);

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public RegistrationMailCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public MimeMessage createMessage(ParticipantRegistration participantRegistration) throws WichtelnMailCreationException {
        try {
            EventDto event = participantRegistration.getEvent();
            ParticipantDto participant = participantRegistration.getParticipant();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            message.setSubject(String.format("You have registered to wichtel at '%s'", event.getTitle()));
            message.setFrom("wichteln@romanboehm.com");
            message.setTo(participant.getEmail());

            Context ctx = new Context();
            ctx.setVariable("event", event);
            ctx.setVariable("participant", participant);
            String textContent = templateEngine.process("registrationmail.txt", ctx);
            message.setText(textContent);

            LOGGER.debug("Created mail for {}", participantRegistration);
            return mimeMessage;
        } catch (MessagingException e) {
            LOGGER.error("Failed to create mail for {}", participantRegistration, e);
            // Re-throw as custom `RuntimeException` to be handled by upstream by `ErrorController`
            throw new WichtelnMailCreationException();
        }
    }
}
