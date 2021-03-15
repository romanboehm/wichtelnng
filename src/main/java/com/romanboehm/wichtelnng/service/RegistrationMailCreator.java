package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.exception.WichtelnMailCreationException;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrationMailCreator {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    

    public MimeMessage createMessage(ParticipantRegistration registration) throws WichtelnMailCreationException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            message.setSubject(String.format("You have registered to wichtel at '%s'", registration.getTitle()));
            message.setFrom("wichteln@romanboehm.com");
            message.setTo(registration.getParticipantEmail());

            Context ctx = new Context();
            ctx.setVariable("registration", registration);
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
