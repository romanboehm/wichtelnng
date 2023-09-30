package com.romanboehm.wichtelnng.usecases.notify;

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

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
class MailToDonorComposer {

    private final Logger log = LoggerFactory.getLogger(MailToDonorComposer.class);

    private final String domain;
    private final String from;
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    MailToDonorComposer(
                        @Value("${com.romanboehm.wichtelnng.domain}") String domain,
                        @Value("${com.romanboehm.wichtelnng.mail.from}") String from,
                        TemplateEngine templateEngine,
                        JavaMailSender mailSender) {
        this.domain = domain;
        this.from = from;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    MimeMessage createMessage(MailToDonorData mailToDonorData) throws MessagingException {
        var mimeMessage = mailSender.createMimeMessage();
        var message = new MimeMessageHelper(mimeMessage, UTF_8.toString());
        message.setSubject(format("You have been matched to wichtel at '%s'", mailToDonorData.title()));
        message.setFrom(from);
        message.setTo(mailToDonorData.match().donor().email());

        var ctx = new Context();
        ctx.setVariable("data", mailToDonorData);
        ctx.setVariable("domain", domain);
        var textContent = templateEngine.process("donormail.txt", ctx);
        message.setText(textContent);

        log.debug("Created mail for {}", mailToDonorData);
        return mimeMessage;
    }
}
