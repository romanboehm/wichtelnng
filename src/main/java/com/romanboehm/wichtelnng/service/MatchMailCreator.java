package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.config.MailConfig;
import com.romanboehm.wichtelnng.exception.WichtelnMailCreationException;
import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.ParticipantsMatch;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
public class MatchMailCreator {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public MatchMailCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public MimeMessage createMatchMessage(Event event, ParticipantsMatch match) {
        try {
            ParticipantsMatch.Donor donor = match.getDonor();
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            message.setSubject(String.format("You have been invited to wichtel at '%s'", event.getTitle()));
            message.setFrom(MailConfig.FROM_ADDRESS);
            message.setTo(donor.getEmail());

            Context ctx = new Context();
            ctx.setVariable("event", event);
            ctx.setVariable("donor", donor.getName());
            ctx.setVariable("recipient", match.getRecipient().getName());
            String textContent = templateEngine.process("matchmail.txt", ctx);
            message.setText(textContent);

            return mimeMessage;
        } catch (MessagingException e) {
            // Re-throw as custom `RuntimeException` to be handled by upstream by `ErrorController`
            throw new WichtelnMailCreationException();
        }
    }
}
