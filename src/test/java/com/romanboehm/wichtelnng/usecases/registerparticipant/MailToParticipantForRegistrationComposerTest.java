package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.mail.MailConfig;
import com.romanboehm.wichtelnng.utils.MailUtils;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static jakarta.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

class MailToParticipantForRegistrationComposerTest {

    private MailToParticipantForRegistrationComposer mailCreator;

    @BeforeEach
    void setUp() {
        var templateResolver = new MailConfig().textTemplateResolver();
        var templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(templateResolver);
        mailCreator = new MailToParticipantForRegistrationComposer(
                "https://wichtelnng.romanboehm.com",
                "mail@wichtelnng.romanboehm.com",
                MailUtils.getTemplateEngine(),
                MailUtils.getJavaMailSender());
    }

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        var event = event();
        var registration = new RegistrationForm()
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(MailToParticipantDataForRegistration.from(event, registration));

        assertThat(mail).isNotNull();
        assertThat(mail.getFrom())
                .extracting(Address::toString)
                .containsExactly("mail@wichtelnng.romanboehm.com");
        assertThat(mail.getRecipients(TO))
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

    @Test
    void shouldHandleDataCorrectly() throws IOException, MessagingException {
        var event = event();
        var registration = new RegistrationForm()
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(MailToParticipantDataForRegistration.from(event, registration));

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("You have registered to wichtel at 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey Angus Young,",
                "You have successfully registered to wichtel at 'AC/DC Secret Santa' (https://wichtelnng.romanboehm.com/about)!",
                "You'll be provided with the name of your gift's recipient through an email to angusyoung@acdc.net once everybody has registered.",
                "The gift's monetary value should not exceed AUD 78.50.",
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichtelnng.romanboehm.com"));
    }

}