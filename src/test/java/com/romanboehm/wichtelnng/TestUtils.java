package com.romanboehm.wichtelnng;

import org.mockito.ArgumentMatcher;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class TestUtils {

    public static ArgumentMatcher<MimeMessage> isSentTo(String email) {
        return argument -> {
            try {
                String emailRecipient = argument.getRecipients(Message.RecipientType.TO)[0].toString();
                return email.equals(emailRecipient);
            } catch (MessagingException e) {
                return false;
            }
        };
    }
}
