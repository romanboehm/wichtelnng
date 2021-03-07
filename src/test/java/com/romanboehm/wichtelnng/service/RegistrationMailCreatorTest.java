package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@SpringBootTest
public class RegistrationMailCreatorTest {

    @Autowired
    private RegistrationMailCreator registrationMailCreator;

    @Test
    public void shouldHandleToAndFromCorrectly() throws MessagingException {
        ParticipantRegistration registration = new ParticipantRegistration(TestData.event().dto())
                .setParticipant(TestData.participant().dto());

        MimeMessage mail = registrationMailCreator.createMessage(registration);

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getRecipients(Message.RecipientType.TO))
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

    @Test
    public void shouldHandleDataCorrectly() throws IOException, MessagingException {
        ParticipantRegistration registration = new ParticipantRegistration(TestData.event().dto())
                .setParticipant(TestData.participant().dto());

        MimeMessage mail = registrationMailCreator.createMessage(registration);

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getSubject()).isEqualTo("You have registered to wichtel at 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), Matchers.stringContainsInOrder(
                "Hey Angus Young,",
                "You have successfully registered to wichtel at 'AC/DC Secret Santa' (https://wichtelnng.romanboehm.com/about)!",
                "You'll be provided with the name of your gift's recipient through an email to angusyoung@acdc.net after June 7, 2666 at 6:06:00 AM AEST.",
                "The gift's monetary value should not exceed AUD 78.50.",
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichtelnng.romanboehm.com"
        ));
    }

}