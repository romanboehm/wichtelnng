package com.romanboehm.wichtelnng.utils;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.common.config.MailConfig;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MailUtils {

    private MailUtils() {
    }

    public static List<MimeMessage> findMailFor(GreenMailExtension greenMail, String... recipients) throws MessagingException {
        var recipientsList = Arrays.asList(recipients);
        var retList = new ArrayList<MimeMessage>();
        for (var msg : greenMail.getReceivedMessages()) {
            if (recipientsList.contains(msg.getRecipients(Message.RecipientType.TO)[0].toString())) {
                retList.add(msg);
            }
        }
        return retList;
    }

    public static TemplateEngine getTemplateEngine() {
        var templateResolver = new MailConfig().textTemplateResolver();
        var templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(templateResolver);
        return templateEngine;
    }

    public static JavaMailSender getJavaMailSender() {
        return new JavaMailSenderImpl();
    }
}
