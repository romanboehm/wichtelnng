package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class MatchMailCreator {

    private final String domain;
    private final String from;
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public MatchMailCreator(
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

    MimeMessage createMessage(Event event, Match match) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
            message.setSubject(format("You have been matched to wichtel at '%s'", event.getTitle()));
            message.setFrom(from);
            message.setTo(match.getDonor().getEmail());

            Context ctx = new Context();
            ctx.setVariable("event", event);
            ctx.setVariable("donor", match.getDonor().getName());
            ctx.setVariable("recipient", match.getRecipient().getName());
            ctx.setVariable("domain", domain);
            String textContent = templateEngine.process("matchmail.txt", ctx);
            message.setText(textContent);

            log.debug("Created mail for {} matching {}", event, match);
            return mimeMessage;
        } catch (MessagingException e) {
            log.error("Failed to create mail for {} with {}", event, match, e);
            // Re-throw as `RuntimeException` to be handled by upstream by `ErrorController`
            throw new RuntimeException("Failed to create match mail");
        }
    }
}
