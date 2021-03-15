package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.exception.WichtelnMailCreationException;
import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
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
public class MatchMailCreator {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public MimeMessage createMessage(Event event, Match match) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            message.setSubject(String.format("You have been matched to wichtel at '%s'", event.getTitle()));
            message.setFrom("wichteln@romanboehm.com");
            message.setTo(match.getDonor().getEmail());

            Context ctx = new Context();
            ctx.setVariable("event", event);
            ctx.setVariable("donor", match.getDonor().getName());
            ctx.setVariable("recipient", match.getRecipient().getName());
            String textContent = templateEngine.process("matchmail.txt", ctx);
            message.setText(textContent);

            log.debug("Created mail for {} matching {}", event, match);
            return mimeMessage;
        } catch (MessagingException e) {
            // Re-throw as custom `RuntimeException` to be handled by upstream by `ErrorController`
            throw new WichtelnMailCreationException();
        }
    }
}
